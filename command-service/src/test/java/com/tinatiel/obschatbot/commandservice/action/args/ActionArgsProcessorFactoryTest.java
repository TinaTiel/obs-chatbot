package com.tinatiel.obschatbot.commandservice.action.args;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.commandservice.action.TestAction;
import com.tinatiel.obschatbot.commandservice.dto.CommandArgs;
import com.tinatiel.obschatbot.commandservice.dto.action.Action;
import com.tinatiel.obschatbot.commandservice.dto.action.args.ActionArgsProcessor;
import com.tinatiel.obschatbot.commandservice.dto.action.args.ActionArgsProcessorFactory;
import com.tinatiel.obschatbot.commandservice.dto.action.args.ActionArgsProcessorFactoryImpl;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class ActionArgsProcessorFactoryTest {

  @Test
  void shouldUseActionArgsProcessorIfAvailable() {

    // Given an action args processor that will return an expected action
    ActionArgsProcessor<TestAction> processor = mock(ActionArgsProcessor.class);
    TestAction expectedAction = new TestAction();
    when(processor.process(any(), any())).thenReturn(expectedAction);

    // Given the factory has an action args processor registered
    ActionArgsProcessorFactory factory = new ActionArgsProcessorFactoryImpl(Map.of(
      TestAction.class, processor
    ));

    // And an action that will be processed by it
    Action someAction = new TestAction();

    // When processed
    Action result = factory.process(someAction, mock(CommandArgs.class));

    // Then the expected action is returned
    assertThat(result).usingRecursiveComparison().isEqualTo(expectedAction);

  }

  @Test
  void shouldReturnActionAsIsIfNoProcessorAvailable() {

    // Given the factory has no action args processor registered
    ActionArgsProcessorFactory factory = new ActionArgsProcessorFactoryImpl(new HashMap<>());

    // And an action that will be processed by it
    Action someAction = new TestAction();

    // When processed
    Action result = factory.process(someAction, mock(CommandArgs.class));

    // Then the expected action is returned
    assertThat(result).usingRecursiveComparison().isEqualTo(someAction);

  }

}
