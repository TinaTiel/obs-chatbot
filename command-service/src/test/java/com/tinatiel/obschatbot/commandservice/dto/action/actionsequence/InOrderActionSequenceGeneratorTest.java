package com.tinatiel.obschatbot.commandservice.dto.action.actionsequence;

import static org.assertj.core.api.Assertions.assertThat;

import com.tinatiel.obschatbot.commandservice.dto.action.TestAction;
import com.tinatiel.obschatbot.commandservice.dto.action.Action;
import java.util.List;
import org.junit.jupiter.api.Test;

public class InOrderActionSequenceGeneratorTest {

  InOrderActionSequenceGenerator generator = new InOrderActionSequenceGenerator();

  @Test
  void shouldReturnInOrder() {

    // Given a sequence of actions
    Action action1 = new TestAction();
    Action action2 = new TestAction();
    Action action3 = new TestAction();

    // And a default sequence
    InOrderActionSequence sequence = InOrderActionSequence.builder()
      .actions(List.of(action1, action2, action3))
      .build();

    // When generated
    List<Action> results = generator.generate(sequence);

    // Then they are returned in order
    assertThat(results).containsExactly(action1, action2, action3);

  }

  @Test
  void shouldReturnReversed() {

    // Given a sequence of actions
    Action action1 = new TestAction();
    Action action2 = new TestAction();
    Action action3 = new TestAction();

    // And a reversed sequence
    InOrderActionSequence sequence = InOrderActionSequence.builder()
      .actions(List.of(action1, action2, action3))
      .reverse(true)
      .build();

    // When generated
    List<Action> results = generator.generate(sequence);

    // Then they are returned in order
    assertThat(results).containsExactly(action3, action2, action1);

  }

}
