/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action;

import com.tinatiel.obschatbot.core.client.ActionClient;
import com.tinatiel.obschatbot.core.client.ActionClientFactory;
import com.tinatiel.obschatbot.core.dispatch.CommandRequestContext;

public class RunnableActionFactoryImpl implements RunnableActionFactory {

    private final ActionClientFactory actionClientFactory;

    public RunnableActionFactoryImpl(ActionClientFactory actionClientFactory) {
        this.actionClientFactory = actionClientFactory;
    }

    @Override
    public RunnableAction createForContext(Action action, CommandRequestContext context) {
        // Sanitize inputs
        if(action == null || context == null) throw new IllegalArgumentException("arguments cannot be null");

        // Get the correct client
        ActionClient client = actionClientFactory.getService(action.getActionType());
        if(client == null) throw new IllegalStateException("Could not find instance of client for type: " + action.getActionType());

        // Return the runnable action
        return action.createRunnableAction(client, context);
    }
}
