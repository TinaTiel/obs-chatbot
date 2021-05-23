package com.tinatiel.obschatbot.data.command.mapper.executable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.tinatiel.obschatbot.core.sequencer.ActionSequencer;
import com.tinatiel.obschatbot.core.sequencer.InOrderActionSequencer;
import com.tinatiel.obschatbot.core.sequencer.RandomOrderActionSequencer;
import com.tinatiel.obschatbot.data.command.model.action.ActionDto;
import com.tinatiel.obschatbot.data.command.model.action.ObsSourceVisibilityActionDto;
import com.tinatiel.obschatbot.data.command.model.action.SendMessageActionDto;
import com.tinatiel.obschatbot.data.command.model.action.WaitActionDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.InOrderSequencerDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.RandomOrderSequencerDto;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExecutableSequencerMapperTest {

  ExecutableActionMapper actionMapper;
  ExecutableSequencerMapper mapper;

  @BeforeEach
  void setUp() {
    actionMapper = mock(ExecutableActionMapper.class);
    mapper = new ExecutableSequencerMapperImpl(actionMapper);
  }

  @Test
  void mapInOrderSequencer() {

    // Given a sequencer and actions
    InOrderSequencerDto sequencerDto = InOrderSequencerDto.builder().reversed(true).build();
    List<ActionDto> actions = Arrays.asList(
      SendMessageActionDto.builder().build(),
      WaitActionDto.builder().build(),
      ObsSourceVisibilityActionDto.builder().build()
    );

    // When mapped
    ActionSequencer result = mapper.map(sequencerDto, actions);

    // Then it maps as expected
    assertThat(result).isInstanceOf(InOrderActionSequencer.class);
    InOrderActionSequencer castResult = (InOrderActionSequencer) result;
    assertThat(castResult.isReversed()).isEqualTo(sequencerDto.isReversed());
    assertThat(castResult.listAll()).hasSameSizeAs(actions);
    verify(actionMapper, times(actions.size())).map(any());

  }

  @Test
  void mapRandomOrderSequencer() {
    // Given a sequencer and actions
    RandomOrderSequencerDto sequencerDto = RandomOrderSequencerDto.builder().pickedPerExecution(42).build();
    List<ActionDto> actions = Arrays.asList(
      SendMessageActionDto.builder().build(),
      WaitActionDto.builder().build(),
      ObsSourceVisibilityActionDto.builder().build()
    );

    // When mapped
    ActionSequencer result = mapper.map(sequencerDto, actions);

    // Then it maps as expected
    assertThat(result).isInstanceOf(RandomOrderActionSequencer.class);
    RandomOrderActionSequencer castResult = (RandomOrderActionSequencer) result;
    assertThat(castResult.getPickedPerExecution()).isEqualTo(actions.size()); // business logic overrides ridiculous values
    assertThat(castResult.listAll()).hasSameSizeAs(actions);
    verify(actionMapper, times(actions.size())).map(any());

  }

  @Test
  void nullDefaultsToInOrderNoActions() {
    // Given a null sequencer and actions
    RandomOrderSequencerDto sequencerDto = null;
    List<ActionDto> actions = null;

    // When mapped
    ActionSequencer result = mapper.map(sequencerDto, actions);

    // Then it maps as expected
    assertThat(result).isInstanceOf(InOrderActionSequencer.class);
    InOrderActionSequencer castResult = (InOrderActionSequencer) result;
    assertThat(castResult.isReversed()).isFalse();
    assertThat(castResult.listAll()).isEmpty();
    verifyNoInteractions(actionMapper);
  }
}
