package com.tinatiel.obschatbot.data.command.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.tinatiel.obschatbot.data.command.entity.sequencer.InOrderSequencerEntity;
import com.tinatiel.obschatbot.data.command.entity.sequencer.RandomOrderSequencerEntity;
import com.tinatiel.obschatbot.data.command.entity.sequencer.SequencerEntity;
import com.tinatiel.obschatbot.data.command.model.sequencer.InOrderSequencerDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.RandomOrderSequencerDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.SequencerDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class SequencerMapperTests {

  SequencerMapper mapper = Mappers.getMapper(SequencerMapper.class);

  @Test
  void mapInOrderSequencer() {

    // Given commands
    InOrderSequencerEntity entity = new InOrderSequencerEntity();
    entity.setReversed(true);

    SequencerDto dto = InOrderSequencerDto.builder()
      .reversed(true)
      .build();

    // When mapped
    SequencerDto dtoResult = mapper.map(entity);
    SequencerEntity entityResult = mapper.map(dto);

    // Then they are equal
    assertThat(dtoResult).usingRecursiveComparison().isEqualTo(dto);
    assertThat(entityResult).usingRecursiveComparison().isEqualTo(entity);

  }

  @Test
  void mapRandomOrderSequencer() {

    // Given commands
    RandomOrderSequencerEntity entity = new RandomOrderSequencerEntity();
    entity.setPickedPerExecution(24);

    SequencerDto dto = RandomOrderSequencerDto.builder()
      .pickedPerExecution(24)
      .build();

    // When mapped
    SequencerDto dtoResult = mapper.map(entity);
    SequencerEntity entityResult = mapper.map(dto);

    // Then they are equal
    assertThat(dtoResult).usingRecursiveComparison().isEqualTo(dto);
    assertThat(entityResult).usingRecursiveComparison().isEqualTo(entity);

  }

}
