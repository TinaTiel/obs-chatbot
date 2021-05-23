package com.tinatiel.obschatbot.data.command.mapper;

import com.tinatiel.obschatbot.data.command.entity.sequencer.InOrderSequencerEntity;
import com.tinatiel.obschatbot.data.command.entity.sequencer.RandomOrderSequencerEntity;
import com.tinatiel.obschatbot.data.command.entity.sequencer.SequencerEntity;
import com.tinatiel.obschatbot.data.command.model.sequencer.InOrderSequencerDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.RandomOrderSequencerDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.SequencerDto;
import org.mapstruct.Mapper;

@Mapper
public interface SequencerMapper {

  default SequencerDto map(SequencerEntity entity) {
    if(entity == null) {
      return null;
    } else if (entity instanceof InOrderSequencerEntity) {
      return map((InOrderSequencerEntity) entity);
    } else if (entity instanceof RandomOrderSequencerEntity) {
      return map((RandomOrderSequencerEntity) entity);
    } else {
      throw new RuntimeException("No mapping available");
    }
  }

  default SequencerEntity map(SequencerDto dto) {
    if(dto == null) return null;
    SequencerEntity entity = new SequencerEntity();

    if(dto instanceof InOrderSequencerDto) {
      return map((InOrderSequencerDto) dto);
    } else if (dto instanceof RandomOrderSequencerDto) {
      return map((RandomOrderSequencerDto) dto);
    } else {
      throw new RuntimeException("No mapping available");
    }
  }

  InOrderSequencerDto map(InOrderSequencerEntity entity);
  InOrderSequencerEntity map(InOrderSequencerDto dto);

  RandomOrderSequencerDto map(RandomOrderSequencerEntity entity);
  RandomOrderSequencerEntity map(RandomOrderSequencerDto dto);

}
