/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action.impl;

import com.tinatiel.obschatbot.core.dispatch.CommandRequest;
import com.tinatiel.obschatbot.core.action.ActionType;
import com.tinatiel.obschatbot.core.client.ActionClientFactory;
import com.tinatiel.obschatbot.core.client.obs.ObsClient;

public class ObsSourceVisibilityAction extends AbstractAction<ObsSourceVisibilityAction> {

    private final ActionClientFactory actionClientFactory;

    private final String sceneName;
    private final String sourceName;
    private final boolean visible;

    public ObsSourceVisibilityAction(CommandRequest commandRequest, ActionClientFactory actionClientFactory,
                                     String sceneName, String sourceName, boolean visible) {
        super(ActionType.OBS, commandRequest);
        this.actionClientFactory = actionClientFactory;
        this.sceneName = sceneName;
        this.sourceName = sourceName;
        this.visible = visible;
    }

    public ActionClientFactory getActionServiceFactory() {
        return actionClientFactory;
    }

    public String getSceneName() {
        return sceneName;
    }

    public String getSourceName() {
        return sourceName;
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public ObsSourceVisibilityAction createRunnableClone(CommandRequest context) {
        return new ObsSourceVisibilityAction(context, actionClientFactory, sceneName, sourceName, visible);
    }

    @Override
    public void run() {

        // Sanitize before the run
        if(actionClientFactory == null || commandRequest == null) {
            throw new IllegalStateException("Missing ActionContext or ActionServiceFactory");
        }
        if(sourceName == null) throw new IllegalStateException("Missing source name");

        // Get the client and invoke it
        ObsClient client = (ObsClient) actionClientFactory.getService(actionType);
        client.setSourceVisibility(sceneName, sourceName, visible);

    }

    @Override
    public String toString() {
        return "ObsSourceVisibilityAction{" +
                "actionType=" + actionType +
                ", actionContext=" + commandRequest +
                ", actionServiceFactory=" + actionClientFactory +
                ", sceneName='" + sceneName + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", visible=" + visible +
                '}';
    }
}
