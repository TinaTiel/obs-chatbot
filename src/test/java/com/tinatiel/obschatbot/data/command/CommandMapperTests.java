package com.tinatiel.obschatbot.data.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.tinatiel.obschatbot.data.command.entity.CommandEntity;
import com.tinatiel.obschatbot.data.command.mapper.CommandMapper;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class CommandMapperTests {

  CommandMapper mapper = Mappers.getMapper(CommandMapper.class);

  @Test
  void mapCommand() {

    // Given commands
    CommandEntity entity = new CommandEntity();
    entity.setName("foo");
    entity.setDisabled(true);

    CommandDto dto = CommandDto.builder()
      .name("foo")
      .disabled(true)
      .build();

    // When mapped
    CommandDto dtoResult = mapper.entityToDto(entity);
    CommandEntity entityResult = mapper.dtoToEntity(dto);

    // Then they are equal
    assertThat(dtoResult).usingRecursiveComparison().isEqualTo(dto);
    assertThat(entityResult).usingRecursiveComparison().isEqualTo(entity);

  }

}
