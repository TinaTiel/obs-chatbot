/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request;

import com.tinatiel.obschatbot.core.action.RunnableAction;
import com.tinatiel.obschatbot.core.request.dispatch.SequentialExecutor;
import com.tinatiel.obschatbot.core.request.dispatch.SequentialExecutorImpl;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class Request implements Runnable {

    private final SequentialExecutor executor;
    private final long timoutMs;
    private final List<RunnableAction> actions;

    /**
     * Defines a runnable request, encompassing some run parameters and the actions/args themselves (RunnableAction).
     * @param sequentialExecutor Instance of an executor that will execute the RunnableActions in-order
     * @param timeoutMs The maximum time a list of RunnableActions has to execute. If less than zero, timeout is infinite.
     * @param actions The list of RunnableActions that will be run.
     */
    public Request(SequentialExecutor sequentialExecutor, long timeoutMs, List<RunnableAction> actions) {
        if(sequentialExecutor == null || actions == null) throw new IllegalArgumentException("arguments cannot be null");
        this.executor = sequentialExecutor;
        this.timoutMs = timeoutMs;
        this.actions = actions;
    }

    @Override
    public void run() {
        for(RunnableAction action:actions) executor.execute(action);
        executor.shutdown();
        try {
            if(timoutMs < 0) {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            } else {
                executor.awaitTermination(timoutMs, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    public List<RunnableAction> getActions() {
        return actions;
    }

    public Executor getExecutor() {
        return executor;
    }

    @Override
    public String toString() {
        return "CommandRequest{" +
                "executor=" + executor +
                ", actions=" + actions +
                '}';
    }
}
