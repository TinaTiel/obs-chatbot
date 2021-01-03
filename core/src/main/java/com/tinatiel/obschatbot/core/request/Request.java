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

    public Request(SequentialExecutor executor, long timeoutMs, List<RunnableAction> actions) {
        if(executor == null || actions == null) throw new IllegalArgumentException("arguments cannot be null");
        if(timeoutMs <= 0) throw new IllegalArgumentException("timeout must be greater than zero");
        this.executor = executor;
        this.timoutMs = timeoutMs;
        this.actions = actions;
    }

    @Override
    public void run() {
        for(RunnableAction action:actions) executor.execute(action);
        executor.shutdown();
        try {
            executor.awaitTermination(timoutMs, TimeUnit.MILLISECONDS);
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
