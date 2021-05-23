package com.tinatiel.obschatbot.data.command.mapper.executable;

import com.tinatiel.obschatbot.core.sequencer.ActionSequencer;
import com.tinatiel.obschatbot.data.command.model.action.ActionDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.SequencerDto;
import java.util.List;

public interface ExecutableSequencerMapper {
  ActionSequencer map(SequencerDto dto, List<ActionDto> actions);
}
