package com.tinatiel.obschatbot.commandservice.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.commandservice.action.ActionGeneratorService;
import com.tinatiel.obschatbot.commandservice.action.ActionGeneratorServiceImpl;
import com.tinatiel.obschatbot.commandservice.command.CommandDto;
import com.tinatiel.obschatbot.commandservice.action.TestAction;
import com.tinatiel.obschatbot.commandservice.args.CommandArgs;
import com.tinatiel.obschatbot.commandservice.action.Action;
import com.tinatiel.obschatbot.commandservice.action.system.ExecuteSequenceAction;
import com.tinatiel.obschatbot.commandservice.actionsequence.ActionSequence;
import com.tinatiel.obschatbot.commandservice.actionsequence.ActionSequenceGenerator;
import com.tinatiel.obschatbot.commandservice.actionsequence.TestActionSequence;
import com.tinatiel.obschatbot.commandservice.args.ActionArgsProcessorFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        ExecuteSequenceAction.builder()
          .actionSequence(new TestActionSequence(List.of(
            action31,
            ExecuteSequenceAction.builder().actionSequence(new TestActionSequence(List.of(
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
    ActionArgsProcessorFactory actionArgsProcessorFactory = new TestActionArgsProcessorFactory();

    // When generated with args
    ActionGeneratorService actionGeneratorService = new ActionGeneratorServiceImpl(
      actionArgsProcessorFactory,
      Map.of(
        TestActionSequence.class, generator
      )
    );
    List<Action> results = actionGeneratorService.generate(command, mock(CommandArgs.class));

    // Then a list of actions is generated
    assertThat(results).usingRecursiveComparison().isEqualTo(List.of(
      action1, action2, action31, action321, action322, action33, action4, action5
    ));

  }

  @Test
  void shouldReturnActionsInOrderAsIsIfNoGeneratorAvailableForSequence() {

    // Given an action args processor factory that returns the actions as is
    ActionArgsProcessorFactory actionArgsProcessorFactory = new TestActionArgsProcessorFactory();

    // Given an action generator service
    // and no generators
    ActionGeneratorService actionGeneratorService = new ActionGeneratorServiceImpl(
      actionArgsProcessorFactory,
      new HashMap<>()
    );

    // And a command with a sequence of actions
    ActionSequence actionSequence = mock(ActionSequence.class);
    List<Action> expectedActions = List.of(
      new TestAction(), new TestAction(), new TestAction()
    );
    when(actionSequence.getActions()).thenReturn(expectedActions);
    CommandDto anyCommand = CommandDto.builder()
      .actionSequence(actionSequence)
      .build();

    // When generated against a command with a sequence
    List<Action> results = actionGeneratorService.generate(anyCommand, mock(CommandArgs.class));

    // Then the actions are returned in the order they were provided
    assertThat(results).containsExactlyElementsOf(expectedActions);

  }

  @Test
  void shouldReturnNoActionsIfCommandHasNoSequence() {

    // Given an action generator that will generate a list of actions
    ActionSequenceGenerator<TestActionSequence> generator = new TestActionSequenceGenerator();

    // And an ActionArgsFactory that returns the action passed to it
    ActionArgsProcessorFactory actionArgsProcessorFactory = new TestActionArgsProcessorFactory();
    ActionGeneratorService actionGeneratorService = new ActionGeneratorServiceImpl(
      actionArgsProcessorFactory,
      Map.of(
        TestActionSequence.class, generator
      )
    );

    // And given a command with no sequencer
    CommandDto commandWithoutSequencer = CommandDto.builder().build();

    // When generated
    List<Action> results = actionGeneratorService.generate(commandWithoutSequencer, mock(CommandArgs.class));

    // Then the list is empty
    assertThat(results).isEmpty();

  }

  public static class TestActionSequenceGenerator implements ActionSequenceGenerator<TestActionSequence> {

    @Override
    public List<Action> generate(TestActionSequence actionSequence) {
      return new ArrayList<>(actionSequence.getActions());
    }

  }

  public static class TestActionArgsProcessorFactory implements ActionArgsProcessorFactory {

    @Override
    public Action process(Action action, CommandArgs commandArgs) {
      return action;
    }
  }

}
