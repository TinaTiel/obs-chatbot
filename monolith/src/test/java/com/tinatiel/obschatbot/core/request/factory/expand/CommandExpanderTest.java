/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.factory.expand;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.model.ExecuteCommandAction;
import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.error.CyclicalActionsException;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.sequencer.ActionSequencer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

public class CommandExpanderTest {

    CommandExpander commandExpander;
    Long recursionTimeout = 500L;

    @BeforeEach
    void setUp() {
        commandExpander = new CommandExpanderImpl(recursionTimeout);
    }

    @Test
    void recursionTimeoutLessThanOrZero() {

        // given a recursion timeout is less than zero
        // Then an exception will be thrown
        assertThatThrownBy(() -> {
            new CommandExpanderImpl(-1L);
        }).isInstanceOf(IllegalArgumentException.class);

        // given a recursion timeout is zero
        // Then an exception will be thrown
        assertThatThrownBy(() -> {
            new CommandExpanderImpl(0L);
        }).isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void enumeratorUsesSequencer() {

        // Given a sequencer
        ActionSequencer sequencer = mock(ActionSequencer.class);

        // Given a command using the sequencer
        Command command = new Command()
                .actionSequencer(sequencer);

        // When enumerated
        commandExpander.expand(command);

        // Then the sequencer's getSequence method is called (rather than the listAll method)
        verify(sequencer).nextSequence();

    }

    @Test
    void commandWithActionsReturnsFlatListOfActions() {

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
        List<Action> results = commandExpander.expand(command);

        // And three actions are returned
        assertThat(results).containsExactly(action1, action2, action3);

    }

    @Test
    void commandWithActionsPointingToOtherCommandReturnsFlatListOfActions() {

        // Given a context
        RequestContext context = mock(RequestContext.class);

        // And given an existing command
        ActionSequencer existingSequencer = mock(ActionSequencer.class);
        Action action1 = mock(Action.class);
        Action action2 = mock(Action.class);
        Action action3 = mock(Action.class);
        when(existingSequencer.nextSequence()).thenReturn(Arrays.asList(action1, action2, action3));
        Command existingCommand = new Command().actionSequencer(existingSequencer);

        // And given a command that references the existing command
        ActionSequencer sequencer = mock(ActionSequencer.class);
        ExecuteCommandAction action = spy(new ExecuteCommandAction(existingCommand));
        when(sequencer.nextSequence()).thenReturn(Collections.singletonList(action));
        Command command = new Command().actionSequencer(sequencer);

        // When enumerated
        List<Action> results = commandExpander.expand(command);

        // Then the three actions are returned (excluding the ExecuteCommandAction itself)
        assertThat(results).containsExactly(action1, action2, action3);

    }

    @Test
    void testForCyclicalActionsUsesSequencerListAllMethod() {

        // Given a sequencer
        ActionSequencer sequencer = mock(ActionSequencer.class);

        // Given a command using the sequencer
        Command command = new Command()
                .actionSequencer(sequencer);

        // When checked
        commandExpander.checkForCyclicalActions(command);

        // Then the sequencer's listAll method is called; we want to check ALL possibilities since
        // the nextSequence() method could return a random sequence
        verify(sequencer, never()).nextSequence();
        verify(sequencer).listAll();
    }

    @Test
    void worstCaseScenarioThrowsWrappedExceptionIfCycleOccursDuringEnumeration() {

        // And given an existing command
        ActionSequencer existingSequencer = mock(ActionSequencer.class);
        Command command = spy(new Command().actionSequencer(existingSequencer));

        // And given one of the actions references the command it lives in
        Action action1 = mock(Action.class);
        Action action2 = new ExecuteCommandAction(command); // <- Returns action1, action2, action3
        Action action3 = mock(Action.class);
        when(existingSequencer.nextSequence()).thenReturn(Arrays.asList(action1, action2, action3));

        // When enumerated, then an exception is thrown
        assertThatThrownBy(() -> {
            commandExpander.expand(command);
        }).isInstanceOf(CyclicalActionsException.class);

    }

    @Test
    void checkingCommandForCyclicalActionsThrowsExceptionAsExpected() {

        // Given some commands exist
        ActionSequencer sequencer1 = mock(ActionSequencer.class);
        Command command1 = spy(new Command().name("command1").actionSequencer(sequencer1));

        ActionSequencer sequencer2 = mock(ActionSequencer.class);
        Command command2 = spy(new Command().name("command2").actionSequencer(sequencer2));

        ActionSequencer sequencer3 = mock(ActionSequencer.class);
        Command command3 = spy(new Command().name("command3").actionSequencer(sequencer3));

        ActionSequencer sequencer4 = mock(ActionSequencer.class);
        Command command4 = spy(new Command().name("command4").actionSequencer(sequencer4));

        // And given the commands are connected by actions in a chain, with one causing a loop
        Action action1 = new ExecuteCommandAction(command2);
        givenSequencerReturns(sequencer1, Collections.singletonList(action1));

        Action action2 = new ExecuteCommandAction(command3);
        givenSequencerReturns(sequencer2, Collections.singletonList(action2));

        Action action3 = new ExecuteCommandAction(command2); // loops here
        givenSequencerReturns(sequencer3, Collections.singletonList(action3));

        Action action4 = new ExecuteCommandAction(command4); // shouldn't reach this action
        givenSequencerReturns(sequencer4, Collections.singletonList(action4));

        // When checked, then an exception is thrown describing the chain of execution
        assertThatThrownBy(() -> {
            commandExpander.checkForCyclicalActions(command1);
        }).isInstanceOf(CyclicalActionsException.class)
                .hasMessageContaining(command1.getName())
                .hasMessageContaining(command2.getName())
                .hasMessageContaining(command3.getName())
                .hasMessageNotContaining(command4.getName()); // we don't visit here because the loop was earlier in the chain

    }

    @Test
    void multiThreadedReturnsActionListsAsExpected() {

        int maxNumCommands = 100;
        int minNumActions = 50;
        int maxNumActions = 500;
        int numClients = 750;

        // Given a f**k ton of some randomly generated commands
        Random random = new Random();
        ConcurrentHashMap<Command, List<Action>> mapCommandsToEnumeratedActions = new ConcurrentHashMap<>();
        int numCommands = random.nextInt(maxNumCommands);
        for(int c=0; c < numCommands; c++) {
            int numActions = random.nextInt(maxNumActions) + minNumActions;
            ActionSequencer actionSequencer = mock(ActionSequencer.class);
            List<Action> actions = new ArrayList<>();
            for(int a=0; a < numActions; a++) actions.add(mock(Action.class));
            givenSequencerReturns(actionSequencer, actions);
            Command command = new Command()
                    .name("command-" + (c+1))
                    .actionSequencer(actionSequencer);
            mapCommandsToEnumeratedActions.put(command, new ArrayList<>());
            assertThat(command.getActionSequencer().listAll()).hasSizeGreaterThanOrEqualTo(minNumActions);
        }

        // When a ton of clients try to use this class to enumerate the commands
        ExecutorService executorService = Executors.newFixedThreadPool(numClients);
        for(Command command : mapCommandsToEnumeratedActions.keySet()) {
            executorService.submit(() -> {
                List<Action> enumeratedActions = commandExpander.expand(command);
                mapCommandsToEnumeratedActions.put(command, enumeratedActions);
            });
        }

        // And when we wait for all of them to finish doing their work
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // Then each command is enumerated into the expected number of actions
        for(Map.Entry<Command, List<Action>> entry:mapCommandsToEnumeratedActions.entrySet()) {
            List<Action> actualActions = entry.getValue();
            List<Action> expectedActions = entry.getKey().getActionSequencer().listAll();
            System.out.println(entry.getKey().getName() + " initialized with " + expectedActions.size() + " actions -- Expanded to " + actualActions.size() + " actions");
            assertThat(actualActions).containsExactlyElementsOf(expectedActions);
        }

    }

    private void givenSequencerReturns(ActionSequencer sequencer, List<Action> actions) {
        when(sequencer.nextSequence()).thenReturn(actions);
        when(sequencer.listAll()).thenReturn(actions);
    }

}
