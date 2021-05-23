package com.tinatiel.obschatbot.data.command.mapper.executable;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.sequencer.ActionSequencer;
import com.tinatiel.obschatbot.core.sequencer.InOrderActionSequencer;
import com.tinatiel.obschatbot.core.sequencer.RandomOrderActionSequencer;
import com.tinatiel.obschatbot.data.command.model.action.ActionDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.InOrderSequencerDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.RandomOrderSequencerDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.SequencerDto;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ExecutableSequencerMapperImpl implements ExecutableSequencerMapper{

  private final ExecutableActionMapper actionMapper;

  public ExecutableSequencerMapperImpl(
    ExecutableActionMapper actionMapper) {
    this.actionMapper = actionMapper;
  }

  @Override
  public ActionSequencer map(SequencerDto dto, List<ActionDto> actions) {

    // Map the actions
    List<Action> executableActions = new ArrayList<>();
    if(actions != null) {
      actions.forEach(actionDto -> {
        executableActions.add(actionMapper.map(actionDto));
      });
    }

    // Use the InOrder sequencer as default if not provided
    if(dto == null) {
      return new InOrderActionSequencer(executableActions, false);
    }

    // Else map
    if(dto instanceof InOrderSequencerDto) {
      return new InOrderActionSequencer(
        executableActions,
        ((InOrderSequencerDto) dto).isReversed()
      );
    } else if(dto instanceof RandomOrderSequencerDto) {
      return new RandomOrderActionSequencer(
        executableActions,
        ((RandomOrderSequencerDto) dto).getPickedPerExecution()
      );
    } else {
      throw new RuntimeException("No mapping for " + dto.getClass());
    }

  }


}
