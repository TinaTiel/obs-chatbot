/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request;

import com.tinatiel.obschatbot.core.action.RunnableAction;
import com.tinatiel.obschatbot.core.request.dispatch.SequentialExecutor;
import com.tinatiel.obschatbot.core.request.dispatch.SequentialExecutorImpl;
import com.tinatiel.obschatbot.core.request.queue.ActionCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Request implements Runnable {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final SequentialExecutor executor;
    private final long timoutMs;
//    private final List<RunnableAction> actions;
    private final List<ActionCommand> actionCommands;

//    /**
//     * Defines a runnable request, encompassing some run parameters and the actions/args themselves (RunnableAction).
//     * @param sequentialExecutor Instance of an executor that will execute the RunnableActions in-order
//     * @param timeoutMs The maximum time a list of RunnableActions has to execute. If less than zero, timeout is infinite.
//     * @param actions The list of RunnableActions that will be run.
//     */
//    public Request(SequentialExecutor sequentialExecutor, long timeoutMs, List<RunnableAction> actions) {
//        if(sequentialExecutor == null || actions == null) throw new IllegalArgumentException("arguments cannot be null");
//        this.executor = sequentialExecutor;
//        this.timoutMs = timeoutMs;
//        this.actions = actions;
//        this.actionCommands = null;
//    }

    public Request(SequentialExecutor sequentialExecutor, long timeoutMs, List<ActionCommand> actionCommands) {
        if(sequentialExecutor == null || actionCommands == null) throw new IllegalArgumentException("arguments cannot be null");
        this.executor = sequentialExecutor;
        this.timoutMs = timeoutMs > 0 ? timeoutMs : Integer.MAX_VALUE;
        this.actionCommands = actionCommands;
    }

    @Override
    public void run() {
//        for(RunnableAction action:actions) executor.execute(action);
//        executor.shutdown();
//        try {
//            if(timoutMs < 0) {
//                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
//            } else {
//                executor.awaitTermination(timoutMs, TimeUnit.MILLISECONDS);
//            }
//        } catch (InterruptedException interruptedException) {
//            interruptedException.printStackTrace();
//        }
        for(ActionCommand actionCommand:actionCommands) {
            try {
                actionCommand.get(timoutMs, TimeUnit.MILLISECONDS);
            } catch (InterruptedException interruptedException) {
                log.warn("Interrupted request at: " + actionCommand, interruptedException);
                Thread.currentThread().interrupt();
            } catch (ExecutionException executionException) {
                log.error("Unable to execute actionCommand: " + actionCommand, executionException);
            } catch (TimeoutException e) {
                log.warn("Unable to complete actionCommand " + actionCommand + " before timeout of " + timoutMs + "ms");
            }
        }
    }

    public List<ActionCommand> getActionCommands() {
        return actionCommands;
    }

    public Executor getExecutor() {
        return executor;
    }

    @Override
    public String toString() {
        return "CommandRequest{" +
                "executor=" + executor +
                ", actionCommands=" + actionCommands +
                '}';
    }
}
