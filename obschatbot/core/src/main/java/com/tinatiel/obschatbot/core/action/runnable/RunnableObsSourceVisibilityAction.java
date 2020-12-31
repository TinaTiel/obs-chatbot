/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action.runnable;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.RunnableAction;
import com.tinatiel.obschatbot.core.action.model.ObsSourceVisibilityAction;
import com.tinatiel.obschatbot.core.client.ActionClient;
import com.tinatiel.obschatbot.core.dispatch.CommandRequestContext;
import com.tinatiel.obschatbot.core.action.ActionType;
import com.tinatiel.obschatbot.core.client.ActionClientFactory;
import com.tinatiel.obschatbot.core.client.obs.ObsClient;

public class RunnableObsSourceVisibilityAction implements RunnableAction<ObsSourceVisibilityAction> {

    private final ObsSourceVisibilityAction action;
    private final ObsClient obsClient;
    private final CommandRequestContext context;

    public RunnableObsSourceVisibilityAction(ObsSourceVisibilityAction action, ObsClient obsClient, CommandRequestContext context) {
        this.action = action;
        this.obsClient = obsClient;
        this.context = context;
    }

    @Override
    public void run() {

        // Sanitize before the run
        if(obsClient == null || context == null) {
            throw new IllegalStateException("Missing ActionContext or ActionServiceFactory");
        }
        if(action.getSourceName() == null) throw new IllegalStateException("Missing source name");

        // Get the client and invoke it
        obsClient.setSourceVisibility(action.getSceneName(), action.getSourceName(), action.isVisible());

    }

    @Override
    public CommandRequestContext getRequestContext() {
        return context;
    }

    @Override
    public ObsSourceVisibilityAction getAction() {
        return action;
    }

    @Override
    public ActionClient getClient() { return obsClient; }

    @Override
    public String toString() {
        return "RunnableObsSourceVisibilityAction{" +
                "obsClient=" + obsClient +
                ", action=" + action +
                ", context=" + context +
                '}';
    }
}
