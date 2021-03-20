/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

import com.tinatiel.obschatbot.core.infra.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StateQueueObserver implements QueueObserver<StateEvent> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final BlockingQueue<StateEvent> stateQueue;
    private List<Listener<StateEvent>> listeners = new ArrayList<>();

    public StateQueueObserver(BlockingQueue<StateEvent> stateQueue) {
        this.stateQueue = stateQueue;
        observeQueue();
    }

    private void observeQueue() {

        // Taking from the queue is blocking until an element is found
        // We don't want this to block the rest of the manager, so we
        // are putting it into its own executor thread.
        executorService.execute(() -> {
            while(true) {
                try {
                    StateEvent state = stateQueue.take();
                    notifyListeners(state);
                } catch (InterruptedException interruptedException) {
                    log.error("Client manager was interrupted while taking state", interruptedException);
                }
            }
        });

    }

    @Override
    public void registerListener(Listener<StateEvent> listener) {
        listeners.add(listener);
    }

    @Override
    public void notifyListeners(StateEvent event) {
        listeners.forEach(it -> it.onEvent(event));
    }
}
