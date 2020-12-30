/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.executor;

import com.tinatiel.obschatbot.core.dispatch.CommandRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executor;

public class KeyedFifoExecutorCommandRequestImpl implements KeyedFifoExecutor<CommandRequest> {
    Logger log = LoggerFactory.getLogger(this.getClass());

    private final Map<CommandRequest, Queue<OrderedKeyedRunnable<CommandRequest>>> keyedRunnables;
    private final Executor delegate;

    public KeyedFifoExecutorCommandRequestImpl(Map<CommandRequest, Queue<OrderedKeyedRunnable<CommandRequest>>> keyedRunnables,
                                               Executor delegate) {
        this.keyedRunnables = keyedRunnables;
        this.delegate = delegate;
    }

    @Override
    public void executeKeyedRunnable(OrderedKeyedRunnable<CommandRequest> runnable) {

        // Sanitize inputs
        if(runnable == null) throw new IllegalArgumentException("Runnable cannot be null");
        if(runnable.getKey() == null) throw new IllegalArgumentException("Runnable's key cannot be null");

        // Operate on keyedRunnables; add to the queue if not the first task, otherwise execute it
        // Note that the runnable has a reference to keyedRunnables and will execute what is in its queue
        boolean isFirstEntryForKey = false;
        synchronized (keyedRunnables) {

            // Get the queue. If it doesn't exist yet, then add it to the map.
            Queue<OrderedKeyedRunnable<CommandRequest>> queue = keyedRunnables.get(runnable.getKey());
            if(queue == null) {
                log.debug("Runnable " + runnable + " is first with key '" + runnable.getKey() + "', creating entry");
                isFirstEntryForKey = true;
                queue = new LinkedList<>();
                keyedRunnables.put(runnable.getKey(), queue);
            }

            // If not the first item in the queue, then add to the queue
            if(!isFirstEntryForKey) {
                log.debug("Runnable " + runnable + " is not the only entry in queue with key '" + runnable.getKey() + "'; adding to queue");
                queue.add(runnable);
                log.debug("Queue is now: " + queue);
            }
        }

        // Else since execute can block, it must be executed outside the syncronized block
        if(isFirstEntryForKey) {
            log.debug("Runnable " + runnable + " is only entry in queue; executing directly");
            delegate.execute(runnable);
        }

    }

    @Override
    public void execute(Runnable runnable) {
        // Sanitize inputs
        if(runnable == null) throw new IllegalArgumentException("Runnable cannot be null");

        // If correct type, then delegate to the correct method
        if(runnable instanceof OrderedKeyedRunnable) {
            executeKeyedRunnable((OrderedKeyedRunnable) runnable);
        } else {
            throw new IllegalArgumentException("Runnable must be of type " + KeyedFifoExecutor.class.getSimpleName() );
        }
    }

    @Override
    public OrderedKeyedRunnable<CommandRequest> createKeyedRunnable(Runnable runnable, CommandRequest key) {
        return null;
    }
}
