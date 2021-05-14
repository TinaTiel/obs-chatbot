package com.tinatiel.obschatbot.data.command.mapper;

import com.tinatiel.obschatbot.data.command.entity.CommandEntity;
import com.tinatiel.obschatbot.data.command.entity.sequencer.InOrderSequencerEntity;
import com.tinatiel.obschatbot.data.command.entity.sequencer.SequencerEntity;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.InOrderSequencerDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.SequencerDto;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Maps between Command entities (in the data layer) and DTOs (in the presentation layer).
 */
@Mapper(
  imports = UUID.class,
  uses = {SequencerMapper.class}
)
public interface CommandMapper {

  CommandDto map(CommandEntity entity);

  CommandEntity map(CommandDto dto);

}
