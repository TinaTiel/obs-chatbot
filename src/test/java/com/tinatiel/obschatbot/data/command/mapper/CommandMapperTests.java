package com.tinatiel.obschatbot.data.command.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.tinatiel.obschatbot.data.command.entity.CommandEntity;
import com.tinatiel.obschatbot.data.command.entity.sequencer.SequencerEntity;
import com.tinatiel.obschatbot.data.command.entity.sequencer.SequencerEntity.Type;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.InOrderSequencerDto;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class CommandMapperTests {

  CommandMapper mapper = Mappers.getMapper(CommandMapper.class);

  @Test
  void mapCommand() {

    // Given commands
    UUID id = UUID.randomUUID();
    CommandEntity entity = new CommandEntity();
    entity.setId(id);
    entity.setName("foo");
    entity.setDisabled(true);

    CommandDto dto = CommandDto.builder()
      .id(id)
      .name("foo")
      .disabled(true)
      .build();

    // When mapped
    CommandDto dtoResult = mapper.map(entity);
    CommandEntity entityResult = mapper.map(dto);

    // Then they are equal
    assertThat(dtoResult).usingRecursiveComparison().isEqualTo(dto);
    assertThat(entityResult).usingRecursiveComparison().isEqualTo(entity);

  }

  @Test
  void mapCommandWithInOrderSequencer() {

    // Given commands
    SequencerEntity sequencerEntity = new SequencerEntity();
    sequencerEntity.setSequencerType(Type.ORDERED);
    sequencerEntity.setReversed(true);
    CommandEntity entity = new CommandEntity();
    entity.setSequencer(sequencerEntity);

    InOrderSequencerDto sequencerDto = InOrderSequencerDto.builder().reversed(true).build();
    CommandDto dto = CommandDto.builder()
      .sequencer(sequencerDto)
      .build();

    // When mapped
    CommandDto dtoResult = mapper.map(entity);
    CommandEntity entityResult = mapper.map(dto);

    // Then they are equal
    assertThat(dtoResult).usingRecursiveComparison().isEqualTo(dto);
    assertThat(entityResult).usingRecursiveComparison().isEqualTo(entity);

  }
}
