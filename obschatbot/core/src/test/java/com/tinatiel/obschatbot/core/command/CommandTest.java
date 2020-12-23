package com.tinatiel.obschatbot.core.command;

import com.tinatiel.obschatbot.core.executor.Executor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class CommandTest {

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
