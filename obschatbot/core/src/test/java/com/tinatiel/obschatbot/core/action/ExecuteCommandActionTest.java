package com.tinatiel.obschatbot.core.action;

import com.tinatiel.obschatbot.core.command.Command;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ExecuteCommandActionTest {

    // This will be replaced. A reference to the command should be stored instead. Commands won't execute, they
    // will merely be read and turned into a list of actions, and those list of Runnables will be passed to an Executor.
    /*
    @Test
    void whenExecutedThenCommandIsExecuted() {

        // Given a com.tinatiel.obschatbot.core.command
        Command command = mock(Command.class);

        // And an com.tinatiel.obschatbot.core.action executing it
        ExecuteCommandAction action = new ExecuteCommandAction(command);

        // When executed
        List<String> args = Arrays.asList("foo", "bar", "baz");
        action.execute(args);

        // Then the com.tinatiel.obschatbot.core.command is executed
        verify(command).execute(args);

    }

    @Disabled
    @Test
    void whenCyclicalExecutionCreatedThenExceptionThrown() {

        // Given a root com.tinatiel.obschatbot.core.command and com.tinatiel.obschatbot.core.executor
        fail("todo");

    }
    */
}
