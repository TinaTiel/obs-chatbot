package com.tinatiel.obschatbot.data.command.mapper;

import com.tinatiel.obschatbot.data.command.entity.sequencer.SequencerEntity;
import com.tinatiel.obschatbot.data.command.entity.sequencer.SequencerEntity.Type;
import com.tinatiel.obschatbot.data.command.model.sequencer.InOrderSequencerDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.RandomOrderSequencerDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.SequencerDto;
import org.mapstruct.Mapper;

@Mapper
public interface SequencerMapper {

  default SequencerDto map(SequencerEntity entity) {
    if(entity == null || entity.getSequencerType() == null) {
      return null;
    } else if (entity.getSequencerType().equals(Type.ORDERED)) {
      return InOrderSequencerDto.builder()
        .reversed(entity.isReversed())
        .build();
    } else if (entity.getSequencerType().equals(Type.RANDOM_ORDER)) {
      return RandomOrderSequencerDto.builder()
        .pickedPerExecution(entity.getPickedPerExecution())
        .build();
    } else {
      throw new RuntimeException("No mapping available");
    }
  }

  default SequencerEntity map(SequencerDto dto) {
    if(dto == null) return null;
    SequencerEntity entity = new SequencerEntity();

    if(dto instanceof InOrderSequencerDto) {
      entity.setSequencerType(Type.ORDERED);
      entity.setReversed(((InOrderSequencerDto) dto).isReversed());
      return entity;
    } else if (dto instanceof RandomOrderSequencerDto) {
      entity.setSequencerType(Type.RANDOM_ORDER);
      entity.setPickedPerExecution(((RandomOrderSequencerDto) dto).getPickedPerExecution());
      return entity;
    } else {
      throw new RuntimeException("No mapping available");
    }
  }

}
