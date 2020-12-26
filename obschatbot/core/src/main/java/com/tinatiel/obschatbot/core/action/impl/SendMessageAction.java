package com.tinatiel.obschatbot.core.action.impl;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.dispatch.CommandRequest;
import com.tinatiel.obschatbot.core.action.ActionType;

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
    public CommandRequest getActionContext() {
        return null;
    }

    @Override
    public Action createRunnableClone(CommandRequest context) {
        return null;
    }
}
