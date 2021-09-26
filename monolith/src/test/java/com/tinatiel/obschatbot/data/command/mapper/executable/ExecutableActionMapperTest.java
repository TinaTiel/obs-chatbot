package com.tinatiel.obschatbot.data.command.mapper.executable;

import static org.assertj.core.api.Assertions.assertThat;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.NoOpAction;
import com.tinatiel.obschatbot.core.action.model.ExecuteCommandAction;
import com.tinatiel.obschatbot.core.action.model.ObsSourceVisibilityAction;
import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.action.model.WaitAction;
import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.sequencer.InOrderActionSequencer;
import com.tinatiel.obschatbot.data.command.model.action.ExecuteCommandActionDto;
import com.tinatiel.obschatbot.data.command.model.action.ObsSourceVisibilityActionDto;
import com.tinatiel.obschatbot.data.command.model.action.SendMessageActionDto;
import com.tinatiel.obschatbot.data.command.model.action.WaitActionDto;
import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class ExecutableActionMapperTest {

  ExecutableActionMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = Mappers.getMapper(ExecutableActionMapper.class);
  }

  @Test
  void mapNull() {
    assertThat(mapper.map(null)).isInstanceOf(NoOpAction.class);
  }

  @Test
  void mapObsSourceVisibilityAction() {

    // Given actions
    ObsSourceVisibilityAction action = new ObsSourceVisibilityAction("scene", "source", true);
    ObsSourceVisibilityActionDto dto = ObsSourceVisibilityActionDto.builder()
      .sceneName("scene")
      .sourceName("source")
      .visible(true)
      .build();

    // When mapped
    Action result = mapper.map(dto);

    // Then they map as expected
    assertThat(result).isInstanceOf(ObsSourceVisibilityAction.class);
    assertThat(result).usingRecursiveComparison().isEqualTo(action);

  }

  @Test
  void mapSendMessageAction() {

    // Given actions
    SendMessageAction action = new SendMessageAction("foo");
    SendMessageActionDto dto = SendMessageActionDto.builder().message("foo").build();

    // When mapped
    Action result = mapper.map(dto);

    // Then they map as expected
    assertThat(result).isInstanceOf(SendMessageAction.class);
    assertThat(result).usingRecursiveComparison().isEqualTo(action);

  }

  @Test
  void mapWaitAction() {

    // Given actions
    WaitAction action = new WaitAction(Duration.ofSeconds(2));
    WaitActionDto dto = WaitActionDto.builder().waitDuration(Duration.ofSeconds(2)).build();

    // When mapped
    Action result = mapper.map(dto);

    // Then they map as expected
    assertThat(result).isInstanceOf(WaitAction.class);
    assertThat(result).usingRecursiveComparison().isEqualTo(action);

  }

}
