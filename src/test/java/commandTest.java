import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class commandTest {

    @Test
    void commandExecutedWithArguments() {

        // Given an executor
        Executor executor = mock(Executor.class);

        // And Given command with actions
        Action action1 = mock(Action.class);
        Action action2 = mock(Action.class);
        Action action3 = mock(Action.class);
        Command command = new Command(executor);
        command.addAction(action1);
        command.addAction(action2);
        command.addAction(action3);

        // When executed with arguments
        List<String> args = Arrays.asList("foo", "bar", "baz");
        command.execute(args);

        // Then the executor is invoked with the actions and arguments
        verify(executor).execute(any(), any());

    }
}
