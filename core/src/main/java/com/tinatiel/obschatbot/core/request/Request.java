/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request;

import com.tinatiel.obschatbot.core.request.dispatch.SequentialExecutor;
import com.tinatiel.obschatbot.core.request.queue.ActionCommand;
import com.tinatiel.obschatbot.core.request.queue.MainQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Request implements Runnable {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final MainQueue mainQueue;
    private final long timoutMs;
    private final List<ActionCommand> actionCommands;

    /**
      * Defines a runnable request, encompassing some run parameters and the actions/args themselves (RunnableAction).
      * @param mainQueue Reference to main queue
      * @param timeoutMs The maximum time an individual actionCommand has to execute. If less than zero, there is no timeout.
      * @param actionCommands The list of actionCommands (completableFutures) that will be run.
      */
    public Request(MainQueue mainQueue, long timeoutMs, List<ActionCommand> actionCommands) {
        if(mainQueue == null || actionCommands == null) throw new IllegalArgumentException("arguments cannot be null");
        this.mainQueue = mainQueue;
        this.timoutMs = timeoutMs > 0 ? timeoutMs : Integer.MAX_VALUE;
        this.actionCommands = actionCommands;
    }

    @Override
    public void run() {
        for(ActionCommand actionCommand:actionCommands) {
            try {
                mainQueue.add(actionCommand);
                // TODO add intelligent logic here for Wait actions; wait at least as long as the wait, plus the specified timeout
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

    public long getTimoutMs() {
        return timoutMs;
    }

    public List<ActionCommand> getActionCommands() {
        return actionCommands;
    }

    @Override
    public String toString() {
        return "CommandRequest{" +
                ", actionCommands=" + actionCommands +
                '}';
    }
}
