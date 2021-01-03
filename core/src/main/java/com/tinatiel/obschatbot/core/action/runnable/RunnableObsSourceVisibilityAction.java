/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action.runnable;

import com.tinatiel.obschatbot.core.action.RunnableAction;
import com.tinatiel.obschatbot.core.action.model.ObsSourceVisibilityAction;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.client.obs.ObsClient;

public class RunnableObsSourceVisibilityAction implements RunnableAction<ObsClient, ObsSourceVisibilityAction> {

    private final ObsSourceVisibilityAction action;
    private final ObsClient obsClient;
    private final RequestContext context;

    public RunnableObsSourceVisibilityAction(ObsSourceVisibilityAction action, ObsClient obsClient, RequestContext context) {
        if(action == null || obsClient == null || context == null) throw new IllegalArgumentException("arguments cannot be null");
        this.action = action;
        this.obsClient = obsClient;
        this.context = context;
    }

    @Override
    public void run() {

        if(action.getSourceName() == null) throw new IllegalStateException("Missing source name");

        // Get the client and invoke it
        obsClient.setSourceVisibility(action.getSceneName(), action.getSourceName(), action.isVisible());

    }

    @Override
    public RequestContext getRequestContext() {
        return context;
    }

    @Override
    public ObsSourceVisibilityAction getAction() {
        return action;
    }

    @Override
    public ObsClient getClient() { return obsClient; }

    @Override
    public String toString() {
        return "RunnableObsSourceVisibilityAction{" +
                "obsClient=" + obsClient +
                ", action=" + action +
                ", context=" + context +
                '}';
    }
}