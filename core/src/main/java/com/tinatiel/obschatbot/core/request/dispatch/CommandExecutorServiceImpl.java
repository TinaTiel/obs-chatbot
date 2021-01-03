/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.dispatch;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CommandExecutorServiceImpl extends ThreadPoolExecutor implements CommandExecutorService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final long requestTimeoutMs;

    public CommandExecutorServiceImpl(long requestTimeoutMs, int maxThreads) {
        super(maxThreads, maxThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        this.requestTimeoutMs = requestTimeoutMs;
    }

    @Override
    public SequentialExecutor newSequentialExecutor() {
        return null;
    }

    @Override
    public long getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);

        // Try to acquire the run lock

    }

}
