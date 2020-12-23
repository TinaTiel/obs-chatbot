package com.tinatiel.obschatbot.core.action;

import com.tinatiel.obschatbot.core.action.enumerator.ActionEnumerator;
import com.tinatiel.obschatbot.core.action.enumerator.ActionEnumeratorImpl;
import com.tinatiel.obschatbot.core.action.sequencer.ActionSequencer;
import com.tinatiel.obschatbot.core.command.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        // We need to address how the actions are cloned first.
        fail("to do");
    }

    @Test
    void commandWithActionsPointingToOtherCommandsReturnsFlatListOfActions() {
        // We need to address this actionType first
        fail("to do");
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
    void testForCyclicalActionsFailsAsExpected() {
        // We need to address this actionType first
        fail("to do");
    }

    @Test
    void testForCyclicalActionsPassesAsExpected() {
        // We need to address this actionType first
        fail("to do");
    }

    @Test
    void cyclicalActionsThrowException() {
        // We need to address this actionType first
        fail("to do");
    }
}
