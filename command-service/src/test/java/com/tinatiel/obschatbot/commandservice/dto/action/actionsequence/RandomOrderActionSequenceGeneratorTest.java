package com.tinatiel.obschatbot.commandservice.dto.action.actionsequence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tinatiel.obschatbot.commandservice.dto.action.TestAction;
import com.tinatiel.obschatbot.commandservice.dto.action.Action;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

public class RandomOrderActionSequenceGeneratorTest {

  RandomSequenceGenerator generator = new RandomSequenceGenerator();

  @RepeatedTest(30)
  void shouldReturnQuantityOfActionsExpectedInOrder() {

    // Given a sequence with some actions
    // And a quantity to pick
    // In same order
    List<Action> actions = createActions(100);
    int pickQty = 30;
    RandomSequence sequence = RandomSequence.builder()
      .actions(actions)
      .pickQty(pickQty)
      .maintainOrder(true)
      .build();

    // When sequenced
    List<Action> results = generator.generate(sequence);

    // Then the quantity returned should match the quantity to pick
    assertThat(results).hasSize(pickQty);
    assertThat(containsAllFrom(results, actions)).isTrue();
    assertThat(containsSubsequenceInOrderFrom(results, actions)).isTrue();

  }

  @RepeatedTest(30)
  void shouldReturnQuantityOfActionsExpectedInRandomOrder() {

    // Given a sequence with some actions
    // And a quantity to pick
    // In random order
    List<Action> actions = createActions(100);
    int pickQty = 30;
    RandomSequence sequence = RandomSequence.builder()
      .actions(actions)
      .pickQty(pickQty)
      .maintainOrder(false)
      .build();

    // When sequenced
    List<Action> results = generator.generate(sequence);

    // Then the quantity returned should match the quantity to pick
    assertThat(results).hasSize(pickQty);
    assertThat(containsAllFrom(results, actions)).isTrue();

    // But the order is random
    assertThat(containsSubsequenceInOrderFrom(results, actions)).isFalse();

  }

  @Test
  void shouldReturnAllIfPickQuantityZeroOrNegative() {

    // Given a sequence with some actions
    // And a quantity to pick
    List<Action> actions = createActions(100);
    int pickQty = -1;
    RandomSequence inOrderSequence = RandomSequence.builder()
      .actions(actions)
      .pickQty(pickQty)
      .maintainOrder(true)
      .build();
    RandomSequence randomOrderSequence = RandomSequence.builder()
      .actions(actions)
      .pickQty(pickQty)
      .maintainOrder(false)
      .build();

    // When sequenced
    assertThat(containsAllFrom(generator.generate(inOrderSequence), actions)).isTrue();
    assertThat(containsAllFrom(generator.generate(randomOrderSequence), actions)).isTrue();

  }

  @Test
  void shouldReturnAllIfPickQuantityGreaterThanAvailable() {

    // Given a sequence with some actions
    // And a quantity to pick
    List<Action> actions = createActions(100);
    int pickQty = 101;
    RandomSequence inOrderSequence = RandomSequence.builder()
      .actions(actions)
      .pickQty(pickQty)
      .maintainOrder(true)
      .build();
    RandomSequence randomOrderSequence = RandomSequence.builder()
      .actions(actions)
      .pickQty(pickQty)
      .maintainOrder(false)
      .build();

    // When sequenced
    assertThat(containsAllFrom(generator.generate(inOrderSequence), actions)).isTrue();
    assertThat(containsAllFrom(generator.generate(randomOrderSequence), actions)).isTrue();

  }

  @Test
  void containsAllFromShouldBehaveAsExpected() {

    Action action1 = new TestAction();
    Action action2 = new TestAction();
    Action action3 = new TestAction();

    assertThat(containsAllFrom(List.of(action1, action3), List.of(action1, action2, action3))).isTrue();
    assertThat(containsAllFrom(List.of(action1, action3), List.of(action2))).isFalse();

    assertThatThrownBy(() -> {
      containsAllFrom(List.of(), List.of(action1, action2, action3));
    }).isInstanceOf(IllegalArgumentException.class);

  }

  @Test
  void containsSubsequenceInOrderFromShouldBehaveAsExpected() {

    Action action1 = new TestAction();
    Action action2 = new TestAction();
    Action action3 = new TestAction();
    Action action4 = new TestAction();
    Action action5 = new TestAction();
    List<Action> source = List.of(action1, action2, action3, action4, action5);

    assertThat(containsSubsequenceInOrderFrom(List.of(action1, action3), source)).isTrue();
    assertThat(containsSubsequenceInOrderFrom(List.of(action1, action3, action5), source)).isTrue();
    assertThat(containsSubsequenceInOrderFrom(List.of(action1, action5, action3), source)).isFalse();
    assertThat(containsSubsequenceInOrderFrom(List.of(action1, action3, action4), source)).isTrue();

    assertThatThrownBy(() -> {
      containsSubsequenceInOrderFrom(List.of(), source);
    }).isInstanceOf(IllegalArgumentException.class);

  }

  private List<Action> createActions(int qty) {
    List<Action> actions = new ArrayList<>();
    for(int i = 0; i < qty; i++) {
      actions.add(new TestAction());
    }
    return actions;
  }

  private boolean containsAllFrom(List<Action> actions, List<Action> source) {
    if(actions == null || actions.isEmpty()) {
      throw new IllegalArgumentException(
        "cannot determine if all elements of empty list contains all elements from some other list; "
          + "this result is undefined"
      );
    }
    for(Action action:actions) {
      if(!source.contains(action)) {
        return false;
      }
    }
    return true;
  }

  private boolean containsSubsequenceInOrderFrom(List<Action> actions, List<Action> source) {

    if(actions == null || actions.isEmpty()) {
      throw new IllegalArgumentException(
        "cannot determine if empty list is a subsequence of source; "
          + "this result is undefined"
      );
    }

    // Get the index of the previous source
    int previousSourceIndex = source.indexOf(actions.get(0));

    for(int i = 1; i < actions.size(); i++) {

      // Get the index of the current source
      int currentSourceIndex = source.indexOf(actions.get(i));

      // If the index of the current source is less than the previous
      // then it is out of order
      if(currentSourceIndex < previousSourceIndex) {
        return false;
      }
      previousSourceIndex = currentSourceIndex;
    }

    // If after all items are compared none are wrong,
    // then the subsequence is the same order
    return true;

  }

}
