/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request;

import com.tinatiel.obschatbot.core.messaging.AbstractObsChatbotEvent;
import com.tinatiel.obschatbot.core.remove.queue.MainQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CommandRequest extends AbstractObsChatbotEvent {

    private final RequestContext context;
    private final List<ActionRequest> actionRequests;

    /**
      * Defines a runnable request, encompassing some run parameters and the actions/args themselves (RunnableAction).
      * @param actionRequests The list of actionCommands (completableFutures) that will be run.
      */
    public CommandRequest(RequestContext context, List<ActionRequest> actionRequests) {
        super();
        if(context == null || actionRequests == null) {
            throw new IllegalArgumentException("context and actionRequests are required");
        }
        this.context = context;
        this.actionRequests = actionRequests;
    }

    public RequestContext getContext() {
        return context;
    }

    public List<ActionRequest> getActionCommands() {
        return actionRequests;
    }

    @Override
    public String toString() {
        return "CommandRequest{" +
                super.toString() +
                ", context=" + context +
                ", actionCommands=" + actionRequests +
                '}';
    }
}
