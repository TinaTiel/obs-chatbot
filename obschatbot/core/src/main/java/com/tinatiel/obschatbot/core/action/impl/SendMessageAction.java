package com.tinatiel.obschatbot.core.action.impl;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.ActionContext;
import com.tinatiel.obschatbot.core.action.ActionType;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SendMessageAction implements Action {

    private final String message;

    public SendMessageAction(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void run() {

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
    public Action createRunnableClone(Action action, ActionContext context) {
        return null;
    }
}
