/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.dispatch;

import com.tinatiel.obschatbot.core.action.RunnableAction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CommandRequest implements Runnable {

    private final ExecutorService executor;
    private final List<RunnableAction> actions;

    public CommandRequest(ExecutorService executor, List<RunnableAction> actions) {
        if(executor == null || actions == null) throw new IllegalArgumentException("arguments cannot be null");
        this.executor = executor;
        this.actions = actions;
    }

    @Override
    public void run() {
        for(RunnableAction action:actions) executor.execute(action);
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
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
