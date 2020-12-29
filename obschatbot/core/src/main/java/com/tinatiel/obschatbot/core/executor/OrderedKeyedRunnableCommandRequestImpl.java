package com.tinatiel.obschatbot.core.executor;

import com.tinatiel.obschatbot.core.dispatch.CommandRequest;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executor;

public class OrderedKeyedRunnableCommandRequestImpl implements OrderedKeyedRunnable<CommandRequest> {

    private final Runnable runnable;
    private final CommandRequest key;
    private final Map<CommandRequest, Queue<KeyedFifoExecutor<CommandRequest>>> keyedRunnables;
    private final Executor delegate;

    public OrderedKeyedRunnableCommandRequestImpl(Runnable runnable,
                                                  CommandRequest key,
                                                  Map<CommandRequest, Queue<KeyedFifoExecutor<CommandRequest>>> keyedRunnables,
                                                  Executor delegate) {
        this.runnable = runnable;
        this.key = key;
        this.keyedRunnables = keyedRunnables;
        this.delegate = delegate;
    }

    @Override
    public CommandRequest getKey() {
        return null;
    }

    @Override
    public Queue<OrderedKeyedRunnable<CommandRequest>> getQueue() {
        return null;
    }

    @Override
    public Executor getDelegate() {
        return null;
    }

    @Override
    public void run() {

    }
}
