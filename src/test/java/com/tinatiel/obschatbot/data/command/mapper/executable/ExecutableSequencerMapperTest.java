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
import com.tinatiel.obschatbot.data.command.model.sequencer.InOrderSequencerDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.RandomOrderSequencerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExecutableSequencerMapperTest {

  ExecutableActionMapper actionMapper;
  ExecutableSequencerMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new ExecutableSequencerMapperImpl();
  }

  @Test
  void mapInOrderSequencer() {

    // Given a sequencer and actions
    InOrderSequencerDto sequencerDto = InOrderSequencerDto.builder().reversed(true).build();

    // When mapped
    ActionSequencer result = mapper.map(sequencerDto);

    // Then it maps as expected
    assertThat(result).isInstanceOf(InOrderActionSequencer.class);
    InOrderActionSequencer castResult = (InOrderActionSequencer) result;
    assertThat(castResult.isReversed()).isEqualTo(sequencerDto.isReversed());

  }

  @Test
  void mapRandomOrderSequencer() {
    // Given a sequencer and actions
    RandomOrderSequencerDto sequencerDto = RandomOrderSequencerDto.builder().pickedPerExecution(42).build();

    // When mapped
    ActionSequencer result = mapper.map(sequencerDto);

    // Then it maps as expected
    assertThat(result).isInstanceOf(RandomOrderActionSequencer.class);
    RandomOrderActionSequencer castResult = (RandomOrderActionSequencer) result;
    assertThat(castResult.getPickedPerExecution()).isEqualTo(sequencerDto.getPickedPerExecution());

  }

  @Test
  void nullDefaultsToInOrderNoActions() {
    // Given a null sequencer and actions
    RandomOrderSequencerDto sequencerDto = null;

    // When mapped
    ActionSequencer result = mapper.map(sequencerDto);

    // Then it maps as expected
    assertThat(result).isInstanceOf(InOrderActionSequencer.class);
    InOrderActionSequencer castResult = (InOrderActionSequencer) result;
    assertThat(castResult.isReversed()).isFalse();

  }

}
