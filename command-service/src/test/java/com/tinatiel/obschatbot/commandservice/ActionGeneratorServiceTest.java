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
      Map.of(
        TestActionSequence.class, generator
      )
    );
    CommandArgs args = mock(CommandArgs.class);
    List<Action> results = actionGeneratorService.generate(command, args);

    // Then a list of actions is generated
    assertThat(results).usingRecursiveComparison().isEqualTo(List.of(
      action1, action2, action31, action321, action322, action33, action4, action5
    ));

  }

  @Test
  void shouldThrowExceptionIfNoGeneratorsCanAcceptSequence() {

    // Given an action generator service
    // with any action args factory
    // and no generators
    ActionGeneratorService actionGeneratorService = new ActionGeneratorServiceImpl(
      mock(ActionArgsFactory.class),
      new HashMap<>()
    );

    // And any command
    CommandDto anyCommand = CommandDto.builder()
      .actionSequence(mock(ActionSequence.class))
      .build();

    // When generated against a command with a sequence
    // Then an exception is thrown
    assertThatThrownBy(() -> {
      actionGeneratorService.generate(anyCommand, mock(CommandArgs.class));
    }).isInstanceOf(UnknownActionSequenceException.class);

  }

  @Test
  void shouldReturnNoActionsIfCommandHasNoSequence() {

    // Given an action generator that will generate a list of actions
    ActionSequenceGenerator<TestActionSequence> generator = new TestActionSequenceGenerator();

    // And an ActionArgsFactory that returns the action passed to it
    ActionArgsFactory actionArgsFactory = new TestActionArgsFactory();
    ActionGeneratorService actionGeneratorService = new ActionGeneratorServiceImpl(
      actionArgsFactory,
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
