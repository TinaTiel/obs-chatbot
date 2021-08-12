package com.tinatiel.obschatbot.data.command.mapper.executable;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.model.ExecuteCommandAction;
import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.error.CyclicalActionsException;
import com.tinatiel.obschatbot.core.error.UnexpectedException;
import com.tinatiel.obschatbot.core.sequencer.ActionSequencer;
import com.tinatiel.obschatbot.data.command.CommandEntityService;
import com.tinatiel.obschatbot.data.command.entity.CommandEntityRepository;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import com.tinatiel.obschatbot.data.command.model.action.ActionDto;
import com.tinatiel.obschatbot.data.command.model.action.ExecuteCommandActionDto;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExecutableCommandMapperImpl implements ExecutableCommandMapper {

  private final ExecutableSequencerMapper sequencerMapper;
  private final ExecutableActionMapper actionMapper;
  private final CommandEntityService commandService;

  public ExecutableCommandMapperImpl(
    ExecutableSequencerMapper sequencerMapper,
    ExecutableActionMapper actionMapper,
    CommandEntityService commandService) {
    this.sequencerMapper = sequencerMapper;
    this.actionMapper = actionMapper;
    this.commandService = commandService;
  }

  @Override
  public Command map(CommandDto dto) {

    return privateMap(dto, new ArrayList<>(), dto.getName());

  }

  private Command privateMap(CommandDto dto, List<String> visited, String root) {

    // Update visited
    visited.add(dto.getName());

    // Map command atrributes
    Command command = new Command()
      .name(dto.getName());

    // Map sequencer
    ActionSequencer sequencer = sequencerMapper.map(dto.getSequencer());
    command.actionSequencer(sequencer);

    List<Action> actions = new ArrayList<>();
    for(ActionDto actionDto: dto.getActions()) {
      if(!(actionDto instanceof ExecuteCommandActionDto)) {
        actions.add(actionMapper.map(actionDto));
      } else {
        // Get the persisted command
        CommandDto nestedCommand = commandService
          .findById(((ExecuteCommandActionDto) actionDto).getTarget())
          .orElseThrow(() -> new UnexpectedException("While building list of actions for command '" + dto.getName() + "', encountered executeCommand action with a target command that doesn't exist: id " + ((ExecuteCommandActionDto) actionDto).getTarget()));

        // Check for cycles
        if(visited.contains(nestedCommand.getName())) {
          throw CyclicalActionsException.fromNameAndBreadcrumbs(root, visited);
        }

        // Add actions
        actions.add(new ExecuteCommandAction(this.privateMap(nestedCommand, visited, root)));
      }
    }
    sequencer.setActions(actions);

    return command;
  }

}
