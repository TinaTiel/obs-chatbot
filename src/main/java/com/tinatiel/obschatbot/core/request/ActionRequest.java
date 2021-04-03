/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.messaging.AbstractObsChatbotEvent;

public class ActionRequest extends AbstractObsChatbotEvent {
    private final Action action;
    private final RequestContext context;

    public ActionRequest(RequestContext context, Action action) {
        super();
        this.action = action;
        this.context = context;
    }

    public Action getAction() {
        return action;
    }

    public RequestContext getContext() {
        return context;
    }

    public boolean requiresCompletion() {
        return action.requiresCompletion();
    }

    @Override
    public String toString() {
        return "ActionCommand{" +
                super.toString() +
                ", action=" + action +
                ", context=" + context +
                '}';
    }
}
