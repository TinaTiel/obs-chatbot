import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ExecutorTest {

    @BeforeEach
    void setUp() {

    }

    @Test
    void executeActionsInOrder() {

        // given actions
        Action action1 = mock(Action.class);
        Action action2 = mock(Action.class);
        Action action3 = mock(Action.class);

        // When executed with arguments
        List<String> args = Arrays.asList("foo", "bar", "baz");
        Executor executor = new InOrderExecutor(false);
        executor.execute(Arrays.asList(action1, action2, action3), args);

        // Then the actions are executed in that order with those arguments
        InOrder inOrder = Mockito.inOrder(action1, action2, action3);
        inOrder.verify(action1).execute(args);
        inOrder.verify(action2).execute(args);
        inOrder.verify(action3).execute(args);

    }

    @Test
    void executeActionsInReverseOrder() {

        // given actions
        Action action1 = mock(Action.class);
        Action action2 = mock(Action.class);
        Action action3 = mock(Action.class);

        // When executed with arguments
        List<String> args = Arrays.asList("foo", "bar", "baz");
        Executor executor = new InOrderExecutor(true);
        executor.execute(Arrays.asList(action1, action2, action3), args);

        // Then the actions are executed in that order with those arguments
        InOrder inOrder = Mockito.inOrder(action3, action2, action1);
        inOrder.verify(action3).execute(args);
        inOrder.verify(action2).execute(args);
        inOrder.verify(action1).execute(args);

    }
}
