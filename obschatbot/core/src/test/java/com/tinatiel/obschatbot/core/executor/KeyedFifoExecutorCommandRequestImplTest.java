package com.tinatiel.obschatbot.core.executor;

import com.tinatiel.obschatbot.core.dispatch.CommandRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executor;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;


public class KeyedFifoExecutorCommandRequestImplTest {

    KeyedFifoExecutor<CommandRequest> keyedFifoExecutor;

    Map<CommandRequest, Queue<KeyedFifoExecutor<CommandRequest>>> keyedRunnables;
    Executor delegate;

    @BeforeEach
    void setUp() {
        keyedRunnables = new HashMap<>();
        delegate = mock(Executor.class);
        keyedFifoExecutor = spy(new KeyedFifoExecutorCommandRequestImpl(keyedRunnables, delegate));
    }

    @Test
    void whenKeyNullThrowException() {

        // Given a keyed runnable that has no key
        OrderedKeyedRunnable<CommandRequest> orderedKeyedRunnable = mock(OrderedKeyedRunnable.class);
        when(orderedKeyedRunnable.getKey()).thenReturn(null);

        // When executed, then an exception is thrown
        assertThatThrownBy(() -> {
            keyedFifoExecutor.executeKeyedRunnable(orderedKeyedRunnable);
        }).isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void genericRunnableThrowsException() {

        // Given a generic runnable
        Runnable nonKeyedRunnable = mock(Runnable.class);

        // When executed, then an exception is thrown
        assertThatThrownBy(() -> {
            keyedFifoExecutor.execute(nonKeyedRunnable);
        }).isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void genericRunMethodDelegatesToKeyedRunnableMethod() {

        // Given a keyed runnable that has a key
        OrderedKeyedRunnable<CommandRequest> orderedKeyedRunnable = mock(OrderedKeyedRunnable.class);
        when(orderedKeyedRunnable.getKey()).thenReturn(mock(CommandRequest.class));

        // When executed on the generic method
        keyedFifoExecutor.execute(orderedKeyedRunnable);

        // Then the call is delegated to keyed runnable method
        verify(keyedFifoExecutor).executeKeyedRunnable(orderedKeyedRunnable);

    }

    @Test
    void mapOfRunnableQueuesPopulatedAsExpected() {

        // Given some keys
        CommandRequest key1 = mock(CommandRequest.class);
        CommandRequest key2 = mock(CommandRequest.class);

        // And given runnables returning those keys
        OrderedKeyedRunnable<CommandRequest> task1 = mock(OrderedKeyedRunnable.class);
        when(task1.getKey()).thenReturn(key1);

        OrderedKeyedRunnable<CommandRequest> task2 = mock(OrderedKeyedRunnable.class);
        when(task2.getKey()).thenReturn(key2);

        OrderedKeyedRunnable<CommandRequest> task3 = mock(OrderedKeyedRunnable.class);
        when(task3.getKey()).thenReturn(key1);

        // When executed
        keyedFifoExecutor.executeKeyedRunnable(task1);
        keyedFifoExecutor.executeKeyedRunnable(task2);
        keyedFifoExecutor.executeKeyedRunnable(task3);

        // Then the map will contain the expected qty of entries
        assertThat(keyedRunnables).hasSize(2);

        // And the queue in each entry will contain the expected qty of keyed runnables
        assertThat(keyedRunnables.get(key1)).hasSize(2);
        assertThat(keyedRunnables.get(key2)).hasSize(1);

    }

    @Test
    void createKeyedRunnableContainsReferenceToMapOfRunnablesAndToDelegateExecutor() {

        // Given a runnable and a key
        Runnable runnable = mock(Runnable.class);
        CommandRequest key = mock(CommandRequest.class);

        // When a keyed runnable is created
        OrderedKeyedRunnable<CommandRequest> orderedKeyedRunnable = keyedFifoExecutor.createKeyedRunnable(runnable, key);

        // Then it has the same key, uses the map in the keyed fifo executor
        assertThat(orderedKeyedRunnable.getKey()).isEqualTo(key);
        assertThat(orderedKeyedRunnable.getQueue()).isSameAs(keyedRunnables);
        assertThat(orderedKeyedRunnable.getDelegate()).isSameAs(delegate);

    }
}
