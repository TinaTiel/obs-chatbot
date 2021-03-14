/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.request.queue.ActionCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RequestQueueObserver implements PausableQueueObserver<ActionCommand> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final BlockingQueue<ActionCommand> actionCommandQueue;
    private List<Listener<ActionCommand>> listeners = new ArrayList<>();

    private volatile boolean paused = false;
    private final ReentrantReadWriteLock pauseLock = new ReentrantReadWriteLock();
    private final Lock pauseReadLock = pauseLock.readLock();
    private final Lock pauseWriteLock = pauseLock.writeLock();
    private final Condition pauseCondition = pauseWriteLock.newCondition();

    public RequestQueueObserver(BlockingQueue<ActionCommand> actionCommandQueue) {
        this.actionCommandQueue = actionCommandQueue;
        observe();
    }

    private void observe() {
        executorService.execute(() -> {
            while(true) {
                pauseWriteLock.lock();
                try {
                    if(paused) {
                        log.debug("Paused, waiting");
                        pauseCondition.await();
                    } else {
                        ActionCommand actionCommand = actionCommandQueue.poll(500, TimeUnit.MILLISECONDS);
                        if(actionCommand != null) notifyListeners(actionCommand);
                    }
                } catch (InterruptedException e) {
                    log.error("Client manager was interrupted while taking action command", e);
                } catch (Exception other) {
                    log.error("An uncaught exception occurred: ", other);
                } finally {
                    pauseWriteLock.unlock();
                }
            }
        });
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        pauseWriteLock.lock();
        try {
            paused = false;
            pauseCondition.signalAll();
        } finally {
            pauseWriteLock.unlock();
        }
    }

    @Override
    public void registerListener(Listener<ActionCommand> listener) {
        this.listeners.add(listener);
    }

    @Override
    public void notifyListeners(ActionCommand event) {
        listeners.forEach( it -> {
            it.onEvent(event);
        });
    }
}
