/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.client.ActionClient;
import com.tinatiel.obschatbot.core.request.RequestContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class ActionRequest {
    private final Action action;
    private final RequestContext context;

    public ActionRequest(Action action, RequestContext context) {
        this.action = action;
        this.context = context;
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
                ", action=" + action +
                ", context=" + context +
                '}';
    }
}
