package com.tinatiel.obschatbot.core.action.impl;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.dispatch.CommandRequest;
import com.tinatiel.obschatbot.core.action.ActionType;

public abstract class AbstractAction <T extends AbstractAction<T>> implements Action<T> {

    protected final ActionType actionType;
    protected final CommandRequest commandRequest;

    protected AbstractAction(ActionType actionType, CommandRequest commandRequest) {
        this.actionType = actionType;
        this.commandRequest = commandRequest;
    }

    @Override
    public ActionType getActionType() {
        return actionType;
    }

    @Override
    public CommandRequest getActionContext() {
        return commandRequest;
    }

    @Override
    public abstract T createRunnableClone(CommandRequest context);

    @Override
    public abstract void run();

}
