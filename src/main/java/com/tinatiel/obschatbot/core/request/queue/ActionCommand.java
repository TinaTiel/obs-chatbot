/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.queue;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.client.ActionClient;
import com.tinatiel.obschatbot.core.request.RequestContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class ActionCommand extends CompletableFuture<Void> {
    private final Class<? extends ActionClient> recipient;
    private final Action action;
    private final RequestContext context;

    public ActionCommand(Class<? extends ActionClient> recipient, Action action, RequestContext context) {
        this.recipient = recipient;
        this.action = action;
        this.context = context;
    }

    public Class<? extends ActionClient> getRecipient() {
        return recipient;
    }

    public Action getAction() {
        return action;
    }

    public RequestContext getContext() {
        return context;
    }

    @Override
    public String toString() {
        return "ActionCommand{" +
                "recipient=" + recipient +
                ", action=" + action +
                ", context=" + context +
                '}';
    }
}
