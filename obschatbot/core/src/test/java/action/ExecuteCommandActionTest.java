package action;

import command.Command;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ExecuteCommandActionTest {

    @Test
    void whenExecutedThenCommandIsExecuted() {

        // Given a command
        Command command = mock(Command.class);

        // And an action executing it
        ExecuteCommandAction action = new ExecuteCommandAction(command);

        // When executed
        List<String> args = Arrays.asList("foo", "bar", "baz");
        action.execute(args);

        // Then the command is executed
        verify(command).execute(args);

    }

    @Disabled
    @Test
    void whenCyclicalExecutionCreatedThenExceptionThrown() {

        // Given a root command and executor
        fail("todo");

    }
}
