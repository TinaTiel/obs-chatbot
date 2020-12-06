package command;

import command.Command;
import executor.Executor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class CommandTest {

    @Test
    void commandInvokesExecutorWithArgs() {

        // Given an executor
        Executor executor = mock(Executor.class);

        // And given a command
        Command command = new Command(executor);

        // When executed with arguments
        List<String> args = Arrays.asList("foo", "bar", "baz");
        command.execute(args);

        // Then the executor is invoked with the actions and arguments
        verify(executor).execute(args);

    }
}
