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

    @Test
    void executeActionsInOrder() {

        // given actions
        Action action1 = mock(Action.class);
        Action action2 = mock(Action.class);
        Action action3 = mock(Action.class);

        // When executed with arguments
        List<String> args = Arrays.asList("foo", "bar", "baz");
        Executor executor = new InOrderExecutor();
        executor.execute(Arrays.asList(action1, action2, action3), args);

        // Then the actions are executed in that order with those arguments
        ArgumentCaptor<List<String>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        InOrder inOrder = Mockito.inOrder(action1, action2, action3);
        for(Action action:Arrays.asList(action1, action2, action3)) {
            verify(action).execute(argumentCaptor.capture());
            assertThat(argumentCaptor.getValue()).containsExactlyElementsOf(args);
        }

    }
}
