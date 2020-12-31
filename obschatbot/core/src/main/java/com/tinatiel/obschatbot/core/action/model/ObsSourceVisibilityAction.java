/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action.model;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.ActionType;
import com.tinatiel.obschatbot.core.action.RunnableAction;
import com.tinatiel.obschatbot.core.action.runnable.RunnableObsSourceVisibilityAction;
import com.tinatiel.obschatbot.core.client.ActionClient;
import com.tinatiel.obschatbot.core.client.obs.ObsClient;
import com.tinatiel.obschatbot.core.dispatch.CommandRequestContext;

public class ObsSourceVisibilityAction implements Action<ObsSourceVisibilityAction> {

    private final ActionType ACTION_TYPE = ActionType.OBS;

    private final String sceneName;
    private final String sourceName;
    private final boolean visible;

    public ObsSourceVisibilityAction(String sceneName, String sourceName, boolean visible) {
        this.sceneName = sceneName;
        this.sourceName = sourceName;
        this.visible = visible;
    }

    @Override
    public ActionType getActionType() {
        return ACTION_TYPE;
    }

    @Override
    public ObsSourceVisibilityAction clone() {
        return new ObsSourceVisibilityAction(sceneName, sourceName, visible);
    }

    @Override
    public RunnableAction<ObsSourceVisibilityAction> createRunnableAction(ActionClient client, CommandRequestContext commandRequestContext) {
        if(client == null || commandRequestContext == null) throw new IllegalArgumentException("arguments cannot be null");
        if(client instanceof ObsClient) {
            return new RunnableObsSourceVisibilityAction(clone(), (ObsClient) client, commandRequestContext);
        } else {
            throw new IllegalArgumentException("Unexpected client: " + client);
        }
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