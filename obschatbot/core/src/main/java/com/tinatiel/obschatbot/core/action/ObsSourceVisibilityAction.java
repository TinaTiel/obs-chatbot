package com.tinatiel.obschatbot.core.action;

import com.tinatiel.obschatbot.core.actionservice.ActionServiceFactory;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ObsSourceVisibilityAction implements Action {

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

    }
}
