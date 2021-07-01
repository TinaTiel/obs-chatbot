package com.tinatiel.obschatbot.data.command.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.tinatiel.obschatbot.data.command.entity.action.ExecuteCommandActionEntity;
import com.tinatiel.obschatbot.data.command.entity.action.ObsSourceVisibilityActionEntity;
import com.tinatiel.obschatbot.data.command.entity.action.SendMessageActionEntity;
import com.tinatiel.obschatbot.data.command.entity.action.WaitActionEntity;
import com.tinatiel.obschatbot.data.command.model.action.ExecuteCommandActionDto;
import com.tinatiel.obschatbot.data.command.model.action.ObsSourceVisibilityActionDto;
import com.tinatiel.obschatbot.data.command.model.action.SendMessageActionDto;
import com.tinatiel.obschatbot.data.command.model.action.WaitActionDto;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class ActionMapperTests {

  ActionMapper mapper = Mappers.getMapper(ActionMapper.class);

  @Test
  void mapExecuteCommandAction() {

    // Given actions
    UUID commandId = UUID.randomUUID();
    UUID actionId = UUID.randomUUID();
    ExecuteCommandActionEntity entity = new ExecuteCommandActionEntity();
    entity.setId(actionId);
    entity.setPosition(42);
    entity.setTarget(commandId);
    ExecuteCommandActionDto dto = ExecuteCommandActionDto.builder()
      .id(actionId)
      .position(42)
      .target(commandId)
      .build();

    // When mapped
    ExecuteCommandActionEntity entityResult = mapper.map(dto);
    ExecuteCommandActionDto dtoResult = mapper.map(entity);

    // Then they match as expected
    assertThat(entityResult).usingRecursiveComparison().isEqualTo(entity);
    assertThat(dtoResult).usingRecursiveComparison().isEqualTo(dto);

  }

  @Test
  void mapObsSourceVisibilityAction() {

    // Given actions
    UUID actionId = UUID.randomUUID();
    ObsSourceVisibilityActionEntity entity = new ObsSourceVisibilityActionEntity();
    entity.setId(actionId);
    entity.setPosition(42);
    entity.setSourceName("sourcename");
    entity.setSceneName("scenename");
    entity.setVisible(true);
    ObsSourceVisibilityActionDto dto = ObsSourceVisibilityActionDto.builder()
      .id(actionId)
      .position(42)
      .sourceName("sourcename")
      .sceneName("scenename")
      .visible(true)
      .build();

    // When mapped
    ObsSourceVisibilityActionEntity entityResult = mapper.map(dto);
    ObsSourceVisibilityActionDto dtoResult = mapper.map(entity);

    // Then they match as expected
    assertThat(entityResult).usingRecursiveComparison().isEqualTo(entity);
    assertThat(dtoResult).usingRecursiveComparison().isEqualTo(dto);

  }

  @Test
  void mapSendMessageAction() {

    // Given actions
    UUID actionId = UUID.randomUUID();
    SendMessageActionEntity entity = new SendMessageActionEntity();
    entity.setId(actionId);
    entity.setPosition(42);
    entity.setMessage("howdy");
    SendMessageActionDto dto = SendMessageActionDto.builder()
      .id(actionId)
      .position(42)
      .message("howdy")
      .build();

    // When mapped
    SendMessageActionEntity entityResult = mapper.map(dto);
    SendMessageActionDto dtoResult = mapper.map(entity);

    // Then they match as expected
    assertThat(entityResult).usingRecursiveComparison().isEqualTo(entity);
    assertThat(dtoResult).usingRecursiveComparison().isEqualTo(dto);

  }

  @Test
  void mapWaitAction() {

    // Given actions
    UUID actionId = UUID.randomUUID();
    WaitActionEntity entity = new WaitActionEntity();
    entity.setId(actionId);
    entity.setPosition(42);
    entity.setWaitDuration(Duration.ofSeconds(5));
    WaitActionDto dto = WaitActionDto.builder()
      .id(actionId)
      .position(42)
      .waitDuration(Duration.ofSeconds(5))
      .build();

    // When mapped
    WaitActionEntity entityResult = mapper.map(dto);
    WaitActionDto dtoResult = mapper.map(entity);

    // Then they match as expected
    assertThat(entityResult).usingRecursiveComparison().isEqualTo(entity);
    assertThat(dtoResult).usingRecursiveComparison().isEqualTo(dto);

  }

}
