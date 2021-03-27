/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.remove.dispatch;


import com.tinatiel.obschatbot.core.request.CommandRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class CommandExecutorServiceImpl implements CommandExecutorService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PausableExecutorService delegator;
    private final long pauseTimeoutMs;
    private final int maxConcurrentCommands;

    /**
     * Wraps the PausableExecutorServiceImpl and provides a centralized place for gathering information
     * about queue configuration and for generating new SequentialExecutor instances for new Requests.
     * @param pauseTimeoutMs The maximum time a pause will stay in effect before resuming automatically. To pause permanently, provide a negative value.
     * @param maxConcurrentCommands The maximum number of commands that can execute at the same time. This sets the size of the
     *                              underlying thread pool in PausableExecutorService
     */
    public CommandExecutorServiceImpl(long pauseTimeoutMs, int maxConcurrentCommands) {
        if(maxConcurrentCommands <= 0) throw new IllegalArgumentException("Max concurrent commands must be greater than zero");
        this.pauseTimeoutMs = pauseTimeoutMs;
        this.maxConcurrentCommands = maxConcurrentCommands;
        delegator = new PausableExecutorServiceImpl(
                maxConcurrentCommands, maxConcurrentCommands,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                pauseTimeoutMs
        );
    }

    @Override
    public void pause() {
        delegator.pause();
    }

    @Override
    public void resume() {
        delegator.resume();
    }

    @Override
    public long getPauseTimeoutMs() {
        return pauseTimeoutMs;
    }

    @Override
    public int getMaxConcurrentCommands() {
        return maxConcurrentCommands;
    }

    @Override
    public void submit(CommandRequest commandRequest) {
        log.debug("CommandExecutorService request submitted: " + commandRequest);
//        delegator.submit(commandRequest);
    }

}
