package com.tinatiel.obschatbot.data.command.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import com.tinatiel.obschatbot.data.command.entity.CommandEntity;
import com.tinatiel.obschatbot.data.command.entity.action.SendMessageActionEntity;
import com.tinatiel.obschatbot.data.command.entity.action.WaitActionEntity;
import com.tinatiel.obschatbot.data.command.entity.sequencer.SequencerEntity;
import com.tinatiel.obschatbot.data.command.entity.sequencer.SequencerEntity.Type;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import com.tinatiel.obschatbot.data.command.model.action.SendMessageActionDto;
import com.tinatiel.obschatbot.data.command.model.action.WaitActionDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.InOrderSequencerDto;
import java.time.Duration;
import java.util.Arrays;
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

  @Test
  void mapCommandWithActions() {

    // Given commands
    SendMessageActionEntity actionEntity1 = new SendMessageActionEntity();
    actionEntity1.setPosition(24);
    actionEntity1.setMessage("Some message");
    WaitActionEntity actionEntity2 = new WaitActionEntity();
    actionEntity2.setPosition(25);
    actionEntity2.setWaitDuration(Duration.ofSeconds(5));
    CommandEntity entity = new CommandEntity();
    entity.setActions(Arrays.asList(actionEntity1, actionEntity2));

    SendMessageActionDto actionDto1 = SendMessageActionDto.builder()
      .position(24).message("Some message").build();
    WaitActionDto actionDto2 = WaitActionDto.builder()
      .position(25).waitDuration(Duration.ofSeconds(5)).build();
    CommandDto dto = CommandDto.builder()
      .actions(Arrays.asList(actionDto1, actionDto2))
      .build();

    // When mapped
    CommandDto dtoResult = mapper.map(entity);
    CommandEntity entityResult = mapper.map(dto);

    // Then they are equal
    assertThat(dtoResult).usingRecursiveComparison().isEqualTo(dto);
    assertThat(entityResult).usingRecursiveComparison().isEqualTo(entity);

    assertThat(dtoResult.getActions()).usingRecursiveComparison().isEqualTo(dto.getActions());
    assertThat(entityResult.getActions()).usingRecursiveComparison().isEqualTo(entity.getActions());

  }
}
