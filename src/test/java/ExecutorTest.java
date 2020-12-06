import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class ExecutorTest {

    Action action1, action2, action3;
    List<Action> actions = new ArrayList<>();
    List<String> args = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // given actions and arguments
        args.addAll(Arrays.asList("foo", "bar", "baz"));

        action1 = mock(Action.class);
        action2 = mock(Action.class);
        action3 = mock(Action.class);

        actions.add(action1);
        actions.add(action2);
        actions.add(action3);
        assertThat(actions).hasSize(3);
    }

    @Test
    void executeActionsInOrder() {

        // When executed with arguments
        Executor executor = new InOrderExecutor(actions,false);
        executor.execute(args);

        // Then the actions are executed in that order with those arguments
        InOrder inOrder = Mockito.inOrder(action1, action2, action3);
        inOrder.verify(action1).execute(args);
        inOrder.verify(action2).execute(args);
        inOrder.verify(action3).execute(args);

    }

    @Test
    void executeActionsInReverseOrder() {

        // When executed with arguments
        Executor executor = new InOrderExecutor(actions,true);
        executor.execute(args);

        // Then the actions are executed in that order with those arguments
        InOrder inOrder = Mockito.inOrder(action3, action2, action1);
        inOrder.verify(action3).execute(args);
        inOrder.verify(action2).execute(args);
        inOrder.verify(action1).execute(args);

    }

    @Test
    void executeRandomActionsIsPseudoRandomAndCyclical() {

        // Given executor will execute two of the three
        Executor executor = new RandomExecutor(actions, 2);

        // when executed
        executor.execute(args);

        // Then two of the three will be executed
        int actionsWithOneInvocation = 0;
        for(Action action:actions) {
            if(Mockito.mockingDetails(action).getInvocations().size() == 1) {
                actionsWithOneInvocation++;
                verify(action).execute(args);
            }
        }
        assertEquals(2, actionsWithOneInvocation);

        // And when executed again
        executor.execute(args);

        // Then at least one of them will be executed twice
        int actionsWithTwoInvocations = 0;
        for(Action action:actions) {
            if(Mockito.mockingDetails(action).getInvocations().size() == 2) {
                actionsWithTwoInvocations++;
                verify(action, atLeastOnce()).execute(args);
            }
        }
        assertEquals(1, actionsWithTwoInvocations);

    }

    @Test
    void executeRandomActionsWhenNullExecutionsThenExecuteAll() {

        // Given executor with null args
        Executor executor = new RandomExecutor(actions, null);

        // when executed
        executor.execute(args);

        // Then all are invoked
        verify(action1, times(1)).execute(args);
        verify(action2, times(1)).execute(args);
        verify(action3, times(1)).execute(args);

        // And when executed again
        executor.execute(args);

        // Then all are invoked again
        verify(action1, times(2)).execute(args);
        verify(action2, times(2)).execute(args);
        verify(action3, times(2)).execute(args);

    }

    @Test
    void executeRandomTotalActionsConsistentAcrossExecutions() {

        // Given executor
        Executor executor = new RandomExecutor(actions, 2);

        // When executed repeatedly, then the list of actions may change order but contains the same elements
        assertThat(executor.getActions()).containsExactlyInAnyOrderElementsOf(actions);
        executor.execute(args);
        assertThat(executor.getActions()).containsExactlyInAnyOrderElementsOf(actions);
        executor.execute(args);
        assertThat(executor.getActions()).containsExactlyInAnyOrderElementsOf(actions);
        executor.execute(args);
        assertThat(executor.getActions()).containsExactlyInAnyOrderElementsOf(actions);
        executor.execute(args);
        assertThat(executor.getActions()).containsExactlyInAnyOrderElementsOf(actions);

    }

    @Test
    void executeRandomActionsWhenMoreExecutionsThanActionsThenExecuteAll() {

        // Given executor with higher execution than number of actions
        Executor executor = new RandomExecutor(actions, 999);

        // when executed
        executor.execute(args);

        // Then all are invoked
        verify(action1, times(1)).execute(args);
        verify(action2, times(1)).execute(args);
        verify(action3, times(1)).execute(args);

        // And when executed again
        executor.execute(args);

        // Then all are invoked again
        verify(action1, times(2)).execute(args);
        verify(action2, times(2)).execute(args);
        verify(action3, times(2)).execute(args);

    }
}
