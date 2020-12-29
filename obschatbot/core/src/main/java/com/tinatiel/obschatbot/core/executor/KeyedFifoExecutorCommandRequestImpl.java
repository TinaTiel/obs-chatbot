package com.tinatiel.obschatbot.core.executor;

import com.tinatiel.obschatbot.core.dispatch.CommandRequest;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executor;

public class KeyedFifoExecutorCommandRequestImpl implements KeyedFifoExecutor<CommandRequest> {

    private final Map<CommandRequest, Queue<KeyedFifoExecutor<CommandRequest>>> keyedRunnables;
    private final Executor delegate;

    public KeyedFifoExecutorCommandRequestImpl(Map<CommandRequest, Queue<KeyedFifoExecutor<CommandRequest>>> keyedRunnables, Executor delegate) {
        this.keyedRunnables = keyedRunnables;
        this.delegate = delegate;
    }

    @Override
    public void executeKeyedRunnable(OrderedKeyedRunnable orderedKeyedRunnable) {

    }

    @Override
    public void execute(Runnable command) {
        synchronized (keyedRunnables) {

        }
    }

    @Override
    public OrderedKeyedRunnable<CommandRequest> createKeyedRunnable(Runnable runnable, CommandRequest key) {
        return null;
    }
}
