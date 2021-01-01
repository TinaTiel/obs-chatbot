/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action.model;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.RunnableAction;
import com.tinatiel.obschatbot.core.action.runnable.RunnableObsSourceVisibilityAction;
import com.tinatiel.obschatbot.core.client.obs.ObsClient;
import com.tinatiel.obschatbot.core.dispatch.CommandRequestContext;

public class ObsSourceVisibilityAction implements Action<ObsClient, ObsSourceVisibilityAction> {

    private final String sceneName;
    private final String sourceName;
    private final boolean visible;

    public ObsSourceVisibilityAction(String sceneName, String sourceName, boolean visible) {
        this.sceneName = sceneName;
        this.sourceName = sourceName;
        this.visible = visible;
    }

    @Override
    public Class<ObsClient> acceptsClientType() {
        return ObsClient.class;
    }

    @Override
    public ObsSourceVisibilityAction clone() {
        return new ObsSourceVisibilityAction(sceneName, sourceName, visible);
    }

    @Override
    public RunnableAction<ObsClient, ObsSourceVisibilityAction> createRunnableAction(ObsClient client, CommandRequestContext commandRequestContext) {
        if(client == null || commandRequestContext == null) throw new IllegalArgumentException("arguments cannot be null");
        return new RunnableObsSourceVisibilityAction(clone(), client, commandRequestContext);
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
    public String toString() {
        return "ObsSourceVisibilityAction{" +
                "sceneName='" + sceneName + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", visible=" + visible +
                '}';
    }
}