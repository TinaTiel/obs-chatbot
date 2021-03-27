/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.remove.dispatch;

import org.junit.jupiter.api.RepeatedTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class PausableExecutorImplTest {
    
    Logger log = LoggerFactory.getLogger(this.getClass());

    @RepeatedTest(3)
    void testPauseResumeWorksAsExpected() {

        // Given a PauseableExecutor with a thread pool of one (so we can assume
        // in-order execution), and an infinite pause timeout
        PausableExecutorService executor = new PausableExecutorServiceImpl(
                1,1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                -1
        );

        // Given three Runnables that will execute a known amount of time
        Runnable task1 = spy(new TestRunnable(100, "task1"));
        Runnable task2 = spy(new TestRunnable(100, "task2"));
        Runnable task3 = spy(new TestRunnable(100, "task3"));

        // Given we submit them for execution
        executor.submit(task1);
        executor.submit(task2);
        executor.submit(task3);

        // And Given we wait approximately for the first to complete
        try {
            log.debug("Waiting a bit before pausing...");
            Thread.sleep(110);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // When we pause and then wait long enough the third task may have started
        executor.pause();
        try {
            log.debug("Paused!...task-3 should NOT complete");
            Thread.sleep(300);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // When we check the status of the Requests, then the first will have run and
        // the third will have not run. We can't be sure of the second one, it depends on OS scheduling
        verify(task1).run();
        verifyNoInteractions(task3);

        // And then when we resume and wait long enough for the last to complete
        executor.resume();
        try {
            log.debug("Resumed!...All tasks should complete");
            Thread.sleep(300);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // Then the last Request will have run
        verify(task3).run();

    }

    @RepeatedTest(3)
    void testResumeExecutionAfterTimeout() {

        // Given a PauseableExecutor with a thread pool of one (so we can assume
        // in-order execution), and a known pause timeout
        PausableExecutorService executor = new PausableExecutorServiceImpl(
                1,1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                10
        );

        // Given three Runnables that will execute a known amount of time
        Runnable task1 = spy(new TestRunnable(100, "task1"));
        Runnable task2 = spy(new TestRunnable(100, "task2"));
        Runnable task3 = spy(new TestRunnable(100, "task3"));

        // Given we submit them for execution
        executor.submit(task1);
        executor.submit(task2);
        executor.submit(task3);

        // And Given we wait approximately for the first to complete
        try {
            log.debug("Waiting a bit before pausing...");
            Thread.sleep(110);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // When we pause and then wait long enough for pause timeout to have elapsed
        // and for the tasks to have finished executing
        executor.pause();
        try {
            log.debug("Paused! Will resume shortly...");
            Thread.sleep(300);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // When we check the status of the Requests, then e requests will have all run
        verify(task1).run();
        verify(task2).run();
        verify(task3).run();

    }

    private static class TestRunnable implements Runnable {
        private final Logger log = LoggerFactory.getLogger(PausableExecutorImplTest.class);
        private final long sleepTime;
        private final String name;

        public TestRunnable(long sleepTime, String name) {
            this.sleepTime = sleepTime;
            this.name = name;
        }

        @Override
        public void run() {
            try {
                log.debug(this + " STARTED");
                Thread.sleep(sleepTime);
                log.debug(this + " COMPLETED");
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }

        @Override
        public String toString() {
            return String.format("Sleeper-%s (sleep=%dms)", name, sleepTime);
        }
    }
}
