package com.tinatiel.obschatbot.core.executor;

import com.tinatiel.obschatbot.core.dispatch.CommandRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class OrderedKeyedRunnableCommandRequestTest {

    Map<CommandRequest, Queue<KeyedFifoExecutor<CommandRequest>>> keyedRunnables;
    CommandRequest key;
    Queue queue;
    Executor delegate;

    @BeforeEach
    void setUp() {
        key = mock(CommandRequest.class);
        queue = mock(Queue.class);
        keyedRunnables = new HashMap<>();
        keyedRunnables.put(key, queue);
        delegate = mock(Executor.class);
    }

    @Test
    void whenRunCalledThenUnderlyingRunnableCalled() {

        // Given a keyed runnable
        Runnable runnable = mock(Runnable.class);
        OrderedKeyedRunnable<CommandRequest> task = new OrderedKeyedRunnableCommandRequestImpl(runnable, key, keyedRunnables, delegate);

        // when run
        task.run();

        // Then its runnable is run
        verify(runnable).run();

    }

    @Test
    void whenQueueEmptyThenEntryRemovedFromMap() {

        // Given a keyed runnable
        Runnable runnable = mock(Runnable.class);
        OrderedKeyedRunnable<CommandRequest> task = new OrderedKeyedRunnableCommandRequestImpl(runnable, key, keyedRunnables, delegate);

        // And given queue is empty
        assertThat(queue).isEmpty();

        // when run
        task.run();

        // Then its runnable is run
        verify(runnable).run();

        // And the entry is removed from the map
        verify(keyedRunnables).remove(key);

    }

    @Test
    void whenRunCalledAndQueueNotEmptyThenNextTaskIsRun() {

        // Given a key
        CommandRequest key = mock(CommandRequest.class);

        // And Given several runnables
        Runnable runnable1 = mock(Runnable.class);
        OrderedKeyedRunnable<CommandRequest> task1 = new OrderedKeyedRunnableCommandRequestImpl(runnable1, key, keyedRunnables, delegate);

        Runnable runnable2 = mock(Runnable.class);
        OrderedKeyedRunnable<CommandRequest> task2 = new OrderedKeyedRunnableCommandRequestImpl(runnable2, key, keyedRunnables, delegate);

        // And given several are already queued
        when(queue.poll()).thenReturn(task2);

        // When the given task is executed
        runnable1.run();

        // Then the first is executed, and the next is delegated to the delegate executor
        InOrder inOrder = inOrder(runnable1, delegate);
        inOrder.verify(runnable1).run();
        inOrder.verify(delegate).execute(runnable2);

    }
}
