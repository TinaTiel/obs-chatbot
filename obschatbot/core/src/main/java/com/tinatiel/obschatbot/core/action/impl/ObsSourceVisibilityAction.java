package com.tinatiel.obschatbot.core.action.impl;

import com.tinatiel.obschatbot.core.action.ActionContext;
import com.tinatiel.obschatbot.core.action.ActionType;
import com.tinatiel.obschatbot.core.actionservice.ActionServiceFactory;
import com.tinatiel.obschatbot.core.actionservice.obs.ObsClient;

public class ObsSourceVisibilityAction extends AbstractAction<ObsSourceVisibilityAction> {

    private final ActionServiceFactory factory;

    private final String sceneName;
    private final String sourceName;
    private final boolean visible;

    public ObsSourceVisibilityAction(ActionContext context, ActionServiceFactory factory,
                                     String sceneName, String sourceName, boolean visible) {
        super(ActionType.OBS, context);
        this.factory = factory;
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
        if(factory == null || actionContext == null) {
            throw new IllegalStateException("Missing ActionContext or ActionServiceFactory");
        }
        if(sourceName == null) throw new IllegalStateException("Missing source name");

        // Get the client and invoke it
        ObsClient client = (ObsClient) factory.getService(actionType);
        client.setSourceVisibility(sceneName, sourceName, visible);

    }

    @Override
    public ObsSourceVisibilityAction createRunnableClone(ObsSourceVisibilityAction action, ActionContext context) {
        return null;
    }
}
