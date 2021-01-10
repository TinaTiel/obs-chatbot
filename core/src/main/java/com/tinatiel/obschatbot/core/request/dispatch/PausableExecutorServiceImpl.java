/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.dispatch;

import com.tinatiel.obschatbot.core.request.queue.MainQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * An executor that extends ThreadPoolExecutor to add pause/resume functionality, borrowed heavily
 * from the example at in the ThreadPoolExecutor javadocs. We add a pause timeout in this case, as the
 * broadcaster may want execution to automatically resume after X seconds in case they forget to unpause
 */
public class PausableExecutorServiceImpl extends ThreadPoolExecutor implements PausableExecutorService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final long pauseTimeout;

    private boolean paused = false;
    private final Lock pauseLock = new ReentrantLock();
    private final Condition pauseCondition = pauseLock.newCondition();

    /**
     * Represents an extension of Java's ThreadPool executor, but with pausing capabilities.
     * @param pauseTimeout Time to wait before resuming execution after a pause. If set to < 0, then it waits forever until interrupted.
     */
    public PausableExecutorServiceImpl(int corePoolSize,
                                       int maximumPoolSize,
                                       long keepAliveTime,
                                       TimeUnit unit,
                                       BlockingQueue<Runnable> workQueue,
                                       long pauseTimeout) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new LoggingRejectedExecutionHandler());
        this.pauseTimeout = pauseTimeout;
    }

    @Override
    public void pause() {
        if(log.isTraceEnabled()) {
            log.trace("Pause request received on " + this);
        } else {
            log.debug("Pause request received");
        }
        // Set paused to true
        pauseLock.lock();
        try {
            paused = true;
        } finally {
            pauseLock.unlock();
        }

    }

    @Override
    public void resume() {
        if(log.isTraceEnabled()) {
            log.trace("Resume request received on " + this);
        } else {
            log.debug("Resume request received");
        }
        // Set paused to false
        pauseLock.lock();
        try {
            paused = false;
            pauseCondition.signalAll();
        } finally {
            pauseLock.unlock();
        }
    }

    @Override
    public long getPauseTimeoutMs() {
        return pauseTimeout;
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        log.trace(String.format("Trying to execute Runnable %s on Thread %s", r, t.getName()));

        // If paused, then wait for the condition to signal that it is unpaused
        pauseLock.lock();
        try {
            if(pauseTimeout < 0) {
                while(paused) {
                    log.trace(String.format("Thread '%s' is paused. Will await executing Runnable %s", t.getName(), r));
                    pauseCondition.await();
                }
            } else {
                while(paused) {
                    log.trace(String.format("Thread '%s' is paused, will wait up to %dms before resuming execution of Runnable %s", t.getName(), pauseTimeout, r));
                    pauseCondition.await(pauseTimeout, TimeUnit.MILLISECONDS);
                    paused = false;
                    pauseCondition.signalAll();
                    log.debug("Resuming due to pause expired pause timeout");
                }
            }
        } catch (InterruptedException interruptedException) {
            // If interrupted then interrupt the executing thread
            t.interrupt();
        } finally {
            // Release the lock so that it can be acquired again.
            pauseLock.unlock();
        }

    }

    private static class LoggingRejectedExecutionHandler implements RejectedExecutionHandler {

        private final Logger log = LoggerFactory.getLogger(this.getClass());

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            log.error("Rejected execution of " + r + " for executor " + executor);
        }
    }

}
