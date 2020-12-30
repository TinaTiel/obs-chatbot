/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

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
        return key;
    }

    @Override
    public Map<CommandRequest, Queue<KeyedFifoExecutor<CommandRequest>>> getSiblings() {
        return keyedRunnables;
    }

    @Override
    public Executor getDelegate() {
        return delegate;
    }

    @Override
    public void run() {

    }
}
