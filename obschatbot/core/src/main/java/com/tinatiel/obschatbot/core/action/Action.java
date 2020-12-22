package com.tinatiel.obschatbot.core.action;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Action {
    CompletableFuture<ActionResult> execute(List<String> arguments);
}
