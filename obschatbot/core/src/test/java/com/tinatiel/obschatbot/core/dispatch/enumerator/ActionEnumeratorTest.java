/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.dispatch.enumerator;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.dispatch.CommandRequest;
import com.tinatiel.obschatbot.core.action.impl.ExecuteCommandAction;
import com.tinatiel.obschatbot.core.sequencer.ActionSequencer;
import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.error.CyclicalActionsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
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
        CommandRequest context = mock(CommandRequest.class);

        // Given a sequencer
        ActionSequencer sequencer = mock(ActionSequencer.class);

        // Given a command using the sequencer
        Command command = new Command()
                .actionSequencer(sequencer);

        // When enumerated
        enumerator.enumerate(command, context);

        // Then the sequencer's getSequence method is called (rather than the listAll method)
        verify(sequencer).nextSequence();

    }

    @Test
    void commandWithActionsReturnsFlatListOfActions() {

        // Given a context
        CommandRequest context = mock(CommandRequest.class);

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
        List<Action> results = enumerator.enumerate(command, context);

        // Then clone is called on each action
        verify(action1).createRunnableClone(context);
        verify(action2).createRunnableClone(context);
        verify(action3).createRunnableClone(context);

        // And three actions are returned
        assertThat(results).hasSize(3);

    }

    @Test
    void commandWithActionsPointingToOtherCommandReturnsFlatListOfActions() {

        // Given a context
        CommandRequest context = mock(CommandRequest.class);

        // And given an existing command
        ActionSequencer existingSequencer = mock(ActionSequencer.class);
        Action action1 = mock(Action.class);
        Action action2 = mock(Action.class);
        Action action3 = mock(Action.class);
        when(existingSequencer.nextSequence()).thenReturn(Arrays.asList(action1, action2, action3));
        Command existingCommand = new Command().actionSequencer(existingSequencer);

        // And given a command that references the existing command
        ActionSequencer sequencer = mock(ActionSequencer.class);
        ExecuteCommandAction action = spy(new ExecuteCommandAction(context, existingCommand));
        when(sequencer.nextSequence()).thenReturn(Collections.singletonList(action));
        Command command = new Command().actionSequencer(sequencer);

        // When enumerated
        List<Action> results = enumerator.enumerate(command, context);

        // Then clone is called on each action
        verify(action1).createRunnableClone(context);
        verify(action2).createRunnableClone(context);
        verify(action3).createRunnableClone(context);

        // but not on the ExecuteCommandAction action
        verify(action, never()).createRunnableClone(any());

        // And three actions are returned
        assertThat(results).hasSize(3);

    }

    @Test
    void testForCyclicalActionsUsesSequencerListAllMethod() {

        // Given a context
        CommandRequest context = mock(CommandRequest.class);

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
    void worstCaseScenarioThrowsWrappedExceptionIfCycleOccursDuringEnumeration() {

        // Given a context
        CommandRequest context = mock(CommandRequest.class);

        // And given an existing command
        ActionSequencer existingSequencer = mock(ActionSequencer.class);
        Command command = spy(new Command().actionSequencer(existingSequencer));

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

    }

    @Test
    void checkingCommandForCyclicalActionsThrowsExceptionAsExpected() {

        // Given a context
        CommandRequest context = mock(CommandRequest.class);

        // Given some commands exist
        ActionSequencer sequencer1 = mock(ActionSequencer.class);
        Command command1 = spy(new Command().name("command1").actionSequencer(sequencer1));

        ActionSequencer sequencer2 = mock(ActionSequencer.class);
        Command command2 = spy(new Command().name("command2").actionSequencer(sequencer2));

        ActionSequencer sequencer3 = mock(ActionSequencer.class);
        Command command3 = spy(new Command().name("command3").actionSequencer(sequencer3));

        ActionSequencer sequencer4 = mock(ActionSequencer.class);
        Command command4 = spy(new Command().name("command4").actionSequencer(sequencer4));

        // And given the commands are connected by actions in a chain, with on causing a loop

        Action action1 = new ExecuteCommandAction(context, command2);
        givenSequencerReturns(sequencer1, Collections.singletonList(action1));

        Action action2 = new ExecuteCommandAction(context, command3);
        givenSequencerReturns(sequencer2, Collections.singletonList(action2));

        Action action3 = new ExecuteCommandAction(context, command2); // loops here
        givenSequencerReturns(sequencer3, Collections.singletonList(action3));

        Action action4 = new ExecuteCommandAction(context, command4); // shouldn't reach this action
        givenSequencerReturns(sequencer4, Collections.singletonList(action4));

        // When checked, then an exception is thrown describing the chain of execution
        assertThatThrownBy(() -> {
            enumerator.checkForCyclicalActions(command1);
        }).isInstanceOf(CyclicalActionsException.class)
                .hasMessageContaining(command1.getName())
                .hasMessageContaining(command2.getName())
                .hasMessageContaining(command3.getName())
                .hasMessageNotContaining(command4.getName()); // we don't visit here because the loop was earlier in the chain

    }

    private void givenSequencerReturns(ActionSequencer sequencer, List<Action> actions) {
        when(sequencer.nextSequence()).thenReturn(actions);
        when(sequencer.listAll()).thenReturn(actions);
    }

}
