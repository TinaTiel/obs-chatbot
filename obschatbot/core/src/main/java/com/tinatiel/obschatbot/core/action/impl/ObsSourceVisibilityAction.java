package com.tinatiel.obschatbot.core.action.impl;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.ActionContext;
import com.tinatiel.obschatbot.core.action.ActionType;
import com.tinatiel.obschatbot.core.actionservice.ActionServiceFactory;
import com.tinatiel.obschatbot.core.actionservice.obs.ObsClient;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ObsSourceVisibilityAction implements Action<ObsSourceVisibilityAction> {

    private final ActionType ACTION_TYPE = ActionType.OBS;

    private final ActionServiceFactory factory;
    private final ActionContext context;

    private final String sceneName;
    private final String sourceName;
    private final boolean visible;

    public ObsSourceVisibilityAction(ActionServiceFactory factory, ActionContext context,
                                     String sceneName, String sourceName, boolean visible) {
        this.factory = factory;
        this.context = context;
        this.sceneName = sceneName;
        this.sourceName = sourceName;
        this.visible = visible;
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
    public void run() {

        // Sanitize before the run
        if(factory == null || context == null) {
            throw new IllegalStateException("Missing ActionContext or ActionServiceFactory");
        }
        if(sourceName == null) throw new IllegalStateException("Missing source name");

        // Get the client and invoke it
        ObsClient client = (ObsClient) factory.getService(ACTION_TYPE);
        client.setSourceVisibility(sceneName, sourceName, visible);

    }

    @Override
    public ActionType getActionType() {
        return null;
    }

    @Override
    public ActionContext getActionContext() {
        return null;
    }

    @Override
    public ObsSourceVisibilityAction createRunnableClone(ObsSourceVisibilityAction action, ActionContext context) {
        return null;
    }
}
