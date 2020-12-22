package com.tinatiel.obschatbot.core.action;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ObsSourceVisibilityAction implements Action {

    private final String sourceName;
    private final Visibility visibility;
    private final Duration duration;

    public ObsSourceVisibilityAction(String sourceName, Visibility visibility, Duration duration) {
        this.sourceName = sourceName;
        this.visibility = visibility;
        this.duration = duration;
    }

    public String getSourceName() {
        return sourceName;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public Duration getDuration() {
        return duration;
    }

    @Override
    public CompletableFuture<ActionResult> execute(List<String> arguments) {
        return null;
    }

}
