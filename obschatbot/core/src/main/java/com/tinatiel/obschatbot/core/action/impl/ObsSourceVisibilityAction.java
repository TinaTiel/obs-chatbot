package com.tinatiel.obschatbot.core.action.impl;

import com.tinatiel.obschatbot.core.action.ActionContext;
import com.tinatiel.obschatbot.core.action.ActionType;
import com.tinatiel.obschatbot.core.actionservice.ActionServiceFactory;
import com.tinatiel.obschatbot.core.actionservice.obs.ObsClient;

public class ObsSourceVisibilityAction extends AbstractAction<ObsSourceVisibilityAction> {

    private final ActionServiceFactory actionServiceFactory;

    private final String sceneName;
    private final String sourceName;
    private final boolean visible;

    public ObsSourceVisibilityAction(ActionContext actionContext, ActionServiceFactory actionServiceFactory,
                                     String sceneName, String sourceName, boolean visible) {
        super(ActionType.OBS, actionContext);
        this.actionServiceFactory = actionServiceFactory;
        this.sceneName = sceneName;
        this.sourceName = sourceName;
        this.visible = visible;
    }

    public ActionServiceFactory getActionServiceFactory() {
        return actionServiceFactory;
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
    public ObsSourceVisibilityAction createRunnableClone(ActionContext context) {
        return new ObsSourceVisibilityAction(context, actionServiceFactory, sceneName, sourceName, visible);
    }

    @Override
    public void run() {

        // Sanitize before the run
        if(actionServiceFactory == null || actionContext == null) {
            throw new IllegalStateException("Missing ActionContext or ActionServiceFactory");
        }
        if(sourceName == null) throw new IllegalStateException("Missing source name");

        // Get the client and invoke it
        ObsClient client = (ObsClient) actionServiceFactory.getService(actionType);
        client.setSourceVisibility(sceneName, sourceName, visible);

    }

    @Override
    public String toString() {
        return "ObsSourceVisibilityAction{" +
                "actionType=" + actionType +
                ", actionContext=" + actionContext +
                ", actionServiceFactory=" + actionServiceFactory +
                ", sceneName='" + sceneName + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", visible=" + visible +
                '}';
    }
}
