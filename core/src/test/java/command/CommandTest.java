package command;

import executor.Executor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class CommandTest {

    @Test
    void commandInvokesExecutorWithArgs() {

        // Given an executor
        Executor executor = mock(Executor.class);

        // And given a command
        Command command = new Command("somename", executor, false);

        // When executed with arguments
        List<String> args = Arrays.asList("foo", "bar", "baz");
        command.execute(args);

        // Then the executor is invoked with the actions and arguments
        verify(executor).execute(args);

    }

    @Test
    void disabledCommandNotExecuted() {

        // Given an executor
        Executor executor = mock(Executor.class);

        // And given a command that is disabled
        Command command = new Command("somename", executor, true);

        // When executed with arguments
        List<String> args = Arrays.asList("foo", "bar", "baz");
        command.execute(args);

        // Then the executor is NOT invoked
        verifyNoInteractions(executor);

    }

}
