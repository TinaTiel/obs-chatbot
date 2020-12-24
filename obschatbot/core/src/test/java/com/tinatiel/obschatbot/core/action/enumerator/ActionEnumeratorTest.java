package com.tinatiel.obschatbot.core.action.enumerator;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.ActionContext;
import com.tinatiel.obschatbot.core.action.enumerator.ActionEnumerator;
import com.tinatiel.obschatbot.core.action.enumerator.ActionEnumeratorImpl;
import com.tinatiel.obschatbot.core.action.impl.ExecuteCommandAction;
import com.tinatiel.obschatbot.core.action.sequencer.ActionSequencer;
import com.tinatiel.obschatbot.core.command.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class ActionEnumeratorTest {

    ActionEnumerator enumerator;

    @BeforeEach
    void setUp() {
        enumerator = new ActionEnumeratorImpl();
    }

    @Test
    void enumeratorUsesSequencer() {

        // Given a context
        ActionContext context = mock(ActionContext.class);

        // Given a sequencer
        ActionSequencer sequencer = mock(ActionSequencer.class);

        // Given a command using the sequencer
        Command command = new Command()
                .actionSequencer(sequencer);

        // When enumerated
        enumerator.enumerate(command, context);

        // Then the sequencer's getSequence method is called (rather than the listAll method)
        verify(sequencer).nextSequence();
        verify(sequencer, never()).listAll();

    }

    @Test
    void commandWithActionsReturnsFlatListOfActions() {

        // Given a context
        ActionContext context = mock(ActionContext.class);

        // Given a sequencer
        ActionSequencer sequencer = mock(ActionSequencer.class);

        // And given actions returned by the sequencer
        Action action1 = mock(Action.class);
        Action action2 = mock(Action.class);
        Action action3 = mock(Action.class);
        when(sequencer.nextSequence()).thenReturn(Arrays.asList(action1, action2, action3));

        // Given a command using the sequencer
        Command command = new Command()
                .actionSequencer(sequencer);

        // When enumerated
        enumerator.enumerate(command, context);

        // Then clone is called on each action
        verify(action1).createRunnableClone(context);
        verify(action2).createRunnableClone(context);
        verify(action3).createRunnableClone(context);

    }

    @Test
    void commandWithActionsPointingToOtherCommandsReturnsFlatListOfActions() {

        // Given a context
        ActionContext context = mock(ActionContext.class);

        // And given an existing command
        ActionSequencer existingSequencer = mock(ActionSequencer.class);
        Command existingCommand = new Command().actionSequencer(existingSequencer);
        Action action1 = mock(Action.class);
        Action action2 = mock(Action.class);
        Action action3 = mock(Action.class);
        when(existingSequencer.nextSequence()).thenReturn(Arrays.asList(action1, action2, action3));

        // Given a separate command + sequencer
        ActionSequencer sequencer = mock(ActionSequencer.class);
        Command command = new Command().actionSequencer(sequencer);

        // And an ExecuteCommand action referencing the existing command
        ExecuteCommandAction action = new ExecuteCommandAction(context, existingCommand);
        when(sequencer.nextSequence()).thenReturn(Collections.singletonList(action));

        // When enumerated
        enumerator.enumerate(command, context);

        // Then clone is called on each action
        verify(action1).createRunnableClone(context);
        verify(action2).createRunnableClone(context);
        verify(action3).createRunnableClone(context);

        // but not on the ExecuteCommandAction action
        verify(action, never()).createRunnableClone(any());

    }

    @Test
    void testForCyclicalActionsUsesSequencerListAllMethod() {

        // Given a context
        ActionContext context = mock(ActionContext.class);

        // Given a sequencer
        ActionSequencer sequencer = mock(ActionSequencer.class);

        // Given a command using the sequencer
        Command command = new Command()
                .actionSequencer(sequencer);

        // When checked
        enumerator.checkForCyclicalActions(command);

        // Then the sequencer's listAll method is called; we want to check ALL possibilities since
        // the nextSequence() method could return a random sequence
        verify(sequencer, never()).nextSequence();
        verify(sequencer).listAll();
    }

    @Test
    void enumeratingOrTestingCommandWithCyclicalActionsFailsAsExpected() {

        // Given a context
        ActionContext context = mock(ActionContext.class);

        // And given an existing command
        ActionSequencer existingSequencer = mock(ActionSequencer.class);
        Command command = new Command().actionSequencer(existingSequencer);

        // And given one of the actions references the command it lives in
        Action action1 = mock(Action.class);
        Action action2 = new ExecuteCommandAction(context, command);
        Action action3 = mock(Action.class);
        when(existingSequencer.nextSequence()).thenReturn(Arrays.asList(action1, action2, action3));
        when(existingSequencer.listAll()).thenReturn(Arrays.asList(action1, action2, action3));

        // When enumerated, then an exception is thrown
        assertThatThrownBy(() -> {
            enumerator.enumerate(command, context);
        }).isInstanceOf(CyclicalActionsException.class);

        // When checked, then an exception is thrown
        assertThatThrownBy(() -> {
            enumerator.checkForCyclicalActions(command);
        }).isInstanceOf(CyclicalActionsException.class);

    }

}
