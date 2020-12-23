package com.tinatiel.obschatbot.core.action;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Action<T extends Action<T>> extends Runnable {
    ActionType getActionType();
    ActionContext getActionContext();
    T createRunnableClone(ActionContext context);
}
