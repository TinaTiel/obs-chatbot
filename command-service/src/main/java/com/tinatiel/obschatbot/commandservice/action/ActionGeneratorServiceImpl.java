package com.tinatiel.obschatbot.commandservice.action;

import com.tinatiel.obschatbot.commandservice.args.CommandArgs;
import com.tinatiel.obschatbot.commandservice.command.CommandDto;
import com.tinatiel.obschatbot.commandservice.action.system.ExecuteSequenceAction;
import com.tinatiel.obschatbot.commandservice.actionsequence.ActionSequence;
import com.tinatiel.obschatbot.commandservice.actionsequence.ActionSequenceGenerator;
import com.tinatiel.obschatbot.commandservice.args.ActionArgsProcessorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ActionGeneratorServiceImpl implements ActionGeneratorService {

  private final ActionSequenceGenerator DEFAULT_GENERATOR = actionSequence -> {
    log.warn("No action sequence generator was registered/found for sequence type " + actionSequence.getClass());
    return actionSequence.getActions();
  };

  private final ActionArgsProcessorFactory actionArgsProcessorFactory;
  private final Map<Class<? extends ActionSequence>, ActionSequenceGenerator<?>> generators;

  @Override
  public List<Action> generate(CommandDto commandDto, CommandArgs commandArgs) {

    // Exit early if there is no sequence
    if(commandDto == null || commandDto.getActionSequence() == null) {
      return new ArrayList<>();
    }

    // Otherwise build and accumulate the list recursively
    List<Action> result = new ArrayList<>();
    generate(result, commandDto.getActionSequence(), commandArgs);
    return result;

  }

  private void generate(List<Action> accumulator, ActionSequence sequence, CommandArgs commandArgs) {

    List<Action> generatedActions = getGenerator(sequence).generate(sequence);
    for (Action action:generatedActions) {
      if(action instanceof ExecuteSequenceAction) {
        this.generate(accumulator, ((ExecuteSequenceAction) action).getActionSequence(), commandArgs);
      } else {
        accumulator.add(actionArgsProcessorFactory.process(action, commandArgs));
      }
    }
  }

  private ActionSequenceGenerator getGenerator(ActionSequence sequence) {
    return generators.getOrDefault(sequence.getClass(), DEFAULT_GENERATOR);
  }

}
