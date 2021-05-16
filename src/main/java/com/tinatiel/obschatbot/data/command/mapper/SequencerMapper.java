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
    if(entity instanceof InOrderSequencerEntity) {
      return this.map((InOrderSequencerEntity) entity);
    } else if (entity instanceof RandomOrderSequencerEntity) {
      return this.map((RandomOrderSequencerEntity) entity);
    } else if (entity == null) {
      return null;
    } else {
      throw new RuntimeException("No mapping available");
    }
  }

  default SequencerEntity map(SequencerDto dto) {
    if(dto instanceof InOrderSequencerDto) {
      return this.map((InOrderSequencerDto) dto);
    } else if (dto instanceof RandomOrderSequencerDto) {
      return this.map((RandomOrderSequencerDto) dto);
    } else if (dto == null) {
      return null;
    } else {
      throw new RuntimeException("No mapping available");
    }
  }

  InOrderSequencerEntity map(InOrderSequencerDto dto);
  InOrderSequencerDto map(InOrderSequencerEntity entity);

  RandomOrderSequencerEntity map(RandomOrderSequencerDto dto);
  RandomOrderSequencerDto map(RandomOrderSequencerEntity entity);

}
