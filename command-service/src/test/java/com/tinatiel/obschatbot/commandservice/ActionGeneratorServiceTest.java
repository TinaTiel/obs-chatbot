package com.tinatiel.obschatbot.commandservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.commandservice.action.TestAction;
import com.tinatiel.obschatbot.commandservice.actionsequence.TestActionSequence;
import com.tinatiel.obschatbot.commandservice.dto.action.actionsequence.UnknownActionSequenceException;
import com.tinatiel.obschatbot.commandservice.dto.CommandArgs;
import com.tinatiel.obschatbot.commandservice.dto.CommandDto;
import com.tinatiel.obschatbot.commandservice.dto.action.Action;
import com.tinatiel.obschatbot.commandservice.dto.action.ExecuteCommandAction;
import com.tinatiel.obschatbot.commandservice.dto.action.actionsequence.ActionSequence;
import com.tinatiel.obschatbot.commandservice.dto.action.actionsequence.ActionSequenceGenerator;
import com.tinatiel.obschatbot.commandservice.dto.action.args.ActionArgsFactory;
import com.tinatiel.obschatbot.commandservice.service.ActionGeneratorService;
import com.tinatiel.obschatbot.commandservice.service.ActionGeneratorServiceImpl;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ActionGeneratorServiceTest {

  @Test
  void shouldRecursivelyReplaceExecuteCommandActionsWithActionList() {

    // Given a command with nested ExecuteCommandActions
    Action action1 = mock(Action.class);
    Action action2 = mock(Action.class);
    Action action31 = mock(Action.class);
    Action action321 = mock(Action.class);
    Action action322 = mock(Action.class);
    Action action33 = mock(Action.class);
    Action action4 = mock(Action.class);
    Action action5 = mock(Action.class);
    CommandDto command = CommandDto.builder()
      .actionSequence(new TestActionSequence(List.of(
        action1,
        action2,
        ExecuteCommandAction.builder()
          .actionSequence(new TestActionSequence(List.of(
            action31,
            ExecuteCommandAction.builder().actionSequence(new TestActionSequence(List.of(
              action321,
              action322
            ))).build(),
            action33))).build(),
        action4,
        action5
      )))
      .build();

    // And an action generator that will generate a list of actions
    ActionSequenceGenerator<TestActionSequence> generator = new TestActionSequenceGenerator();

    // And an ActionArgsFactory that returns the action passed to it
    ActionArgsFactory actionArgsFactory = new TestActionArgsFactory();

    // When generated with args
    ActionGeneratorService actionGeneratorService = new ActionGeneratorServiceImpl(
      actionArgsFactory,
      List.of(generator));
    CommandArgs args = mock(CommandArgs.class);
    List<Action> results = actionGeneratorService.generate(command, args);

    // Then a list of actions is generated
    assertThat(results).usingRecursiveComparison().isEqualTo(List.of(
      action1, action2, action31, action321, action322, action33, action4, action5
    ));

  }

  @Test
  void shouldThrowExceptionIfNoGeneratorsCanAcceptSequence() {

    // Given a bunch of generators that don't accept anything
    ActionSequenceGenerator generator1 = mock(ActionSequenceGenerator.class);
    ActionSequenceGenerator generator2 = mock(ActionSequenceGenerator.class);
    ActionSequenceGenerator generator3 = mock(ActionSequenceGenerator.class);
    when(generator1.accept(any())).thenReturn(false);
    when(generator2.accept(any())).thenReturn(false);
    when(generator3.accept(any())).thenReturn(false);

    // And an ActionArgsFactory
    ActionArgsFactory actionArgsFactory = mock(ActionArgsFactory.class);

    // When generated against a command with a sequence
    // Then an exception is thrown
    CommandDto anyCommand = CommandDto.builder()
      .actionSequence(mock(ActionSequence.class))
      .build();
    ActionGeneratorService actionGeneratorService = new ActionGeneratorServiceImpl(
      actionArgsFactory,
      List.of(
      generator1, generator2, generator3));
    assertThatThrownBy(() -> {
      actionGeneratorService.generate(anyCommand, mock(CommandArgs.class));
    }).isInstanceOf(UnknownActionSequenceException.class);

  }

  @Test
  void shouldUseFirstAcceptingGenerator() {

    // Given a generator that would return actions, but won't accept any generator
    ActionSequenceGenerator<?> generatorIgnored = mock(ActionSequenceGenerator.class);
    when(generatorIgnored.accept(any())).thenReturn(false);
    when(generatorIgnored.generate(any())).thenReturn(List.of(new TestAction()));

    // And a generator that will accept any generator and return some actions
    List<Action> expectedActions = List.of(new TestAction(), new TestAction(), new TestAction());
    ActionSequenceGenerator<?> firstAcceptingGenerator = mock(ActionSequenceGenerator.class);
    when(firstAcceptingGenerator.accept(any())).thenReturn(true);
    when(firstAcceptingGenerator.generate(any())).thenReturn(expectedActions);

    // And another generator that will accept any generator and return some actions
    ActionSequenceGenerator<?> secondAcceptingGenerator = mock(ActionSequenceGenerator.class);
    when(secondAcceptingGenerator.accept(any())).thenReturn(true);
    when(secondAcceptingGenerator.generate(any())).thenReturn(List.of(new TestAction(), new TestAction()));

    // And an ActionArgsFactory that returns the action passed to it
    ActionArgsFactory actionArgsFactory = new TestActionArgsFactory();

    // And given the service is creator with the generators in the above order
    ActionGeneratorService actionGeneratorService = new ActionGeneratorServiceImpl(
      actionArgsFactory,
      List.of(generatorIgnored, firstAcceptingGenerator, secondAcceptingGenerator)
    );

    // When generated against any command with at least one sequence
    CommandDto anyCommand = CommandDto.builder()
      .actionSequence(mock(ActionSequence.class))
      .build();
    List<Action> result = actionGeneratorService.generate(anyCommand, mock(CommandArgs.class));

    // Then the first generator that accepted the action is what is used to return the actions
    assertThat(result).usingRecursiveComparison().isEqualTo(expectedActions);

  }

  @Test
  void shouldNotAcceptNullOrEmptyGenerators() {

    // Given an ActionArgsFactory
    ActionArgsFactory actionArgsFactory = mock(ActionArgsFactory.class);

    assertThatThrownBy(() -> {
      new ActionGeneratorServiceImpl(actionArgsFactory, null);
    }).isInstanceOf(IllegalArgumentException.class);

    assertThatThrownBy(() -> {
      new ActionGeneratorServiceImpl(actionArgsFactory, new ArrayList<>());
    }).isInstanceOf(IllegalArgumentException.class);

  }

  @Test
  void shouldReturnNoActionsIfCommandHasNoSequence() {

    // Given the service has generators that accept any command
    // And an ActionArgsFactory that returns the action passed to it
    ActionArgsFactory actionArgsFactory = new TestActionArgsFactory();
    ActionGeneratorService actionGeneratorService = new ActionGeneratorServiceImpl(
      actionArgsFactory,
      List.of(
      new TestActionSequenceGenerator()
    ));

    // And given a command with no sequencer
    CommandDto commandWithoutSequencer = CommandDto.builder().build();

    // When generated
    List<Action> results = actionGeneratorService.generate(commandWithoutSequencer, mock(CommandArgs.class));

    // Then the list is empty
    assertThat(results).isEmpty();

  }

  public static class TestActionSequenceGenerator implements ActionSequenceGenerator<TestActionSequence> {

    @Override
    public boolean accept(ActionSequence actionSequence) {
      return true;
    }

    @Override
    public List<Action> generate(TestActionSequence actionSequence) {
      return new ArrayList<>(actionSequence.getActions());
    }

  }

  public static class TestActionArgsFactory implements ActionArgsFactory {

    @Override
    public Action process(Action action, CommandArgs commandArgs) {
      return action;
    }
  }

}
