package com.tinatiel.obschatbot.core.command;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.actionsequencer.ActionSequencer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class CommandTest {

    @Test
    void defaults() {

        // Given a default command with no data
        Command command = new Command();

        // Then it has the following defaults
        assertThat(command.getName()).isNull();
        assertThat(command.getActionSequencer()).isNull();
        assertThat(command.getActions()).isEmpty();
        assertThat(command.isDisabled()).isFalse();

    }

    @Test
    void buildCommand() {

        // Given data
        String name = "foo";
        ActionSequencer actionSequencer = mock(ActionSequencer.class);
        boolean disabled = true;

        // When built
        Command command = new Command()
                .name(name)
                .actionSequencer(actionSequencer)
                .disabled(disabled);

        // Then the fields return vals as expected
        assertThat(command.getName()).isEqualTo(name);
        assertThat(command.getActionSequencer()).isEqualTo(actionSequencer);
        assertThat(command.isDisabled()).isEqualTo(disabled);

    }

    @Test
    void actionsDelegatedToSequencer() {

        // Given an action sequencer with actions
        ActionSequencer actionSequencer = mock(ActionSequencer.class);
        Action action1 = mock(Action.class);
        Action action2 = mock(Action.class);
        Action action3 = mock(Action.class);
        List<Action> actions = Arrays.asList(action1, action2, action3);
        when(actionSequencer.getActions()).thenReturn(actions);

        // And a command is assigned the sequencer
        Command command = new Command().actionSequencer(actionSequencer);

        // When called for actions
        List<Action> result = command.getActions();

        // Then the (immutable) result is delegated to the sequencer
        verify(actionSequencer, times(1)).getActions();
        assertThat(result).containsExactlyElementsOf(actions);
        assertThat(result).isNotSameAs(actions); // we don't want the actions to be changed from the command

    }

    // Commands won't be executed directly, they will be used in a factory method to generate a list of executable
    // actions which have their execution context ("args") inside each of them so they can be executed as Runnables
    // in an Executor and/or queue
    /*
    @Test
    void commandInvokesExecutorWithArgs() {

        // Given an com.tinatiel.obschatbot.core.executor
        Executor executor = mock(Executor.class);

        // And given a com.tinatiel.obschatbot.core.command
        Command command = new Command("somename", executor, false);

        // When executed with arguments
        List<String> args = Arrays.asList("foo", "bar", "baz");
        command.execute(args);

        // Then the com.tinatiel.obschatbot.core.executor is invoked with the actions and arguments
        verify(executor).execute(args);

    }

    @Test
    void disabledCommandNotExecuted() {

        // Given an com.tinatiel.obschatbot.core.executor
        Executor executor = mock(Executor.class);

        // And given a com.tinatiel.obschatbot.core.command that is disabled
        Command command = new Command("somename", executor, true);

        // When executed with arguments
        List<String> args = Arrays.asList("foo", "bar", "baz");
        command.execute(args);

        // Then the com.tinatiel.obschatbot.core.executor is NOT invoked
        verifyNoInteractions(executor);

    }
    */

}
