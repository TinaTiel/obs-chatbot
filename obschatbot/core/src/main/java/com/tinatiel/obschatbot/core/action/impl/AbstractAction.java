package com.tinatiel.obschatbot.core.action.impl;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.ActionContext;
import com.tinatiel.obschatbot.core.action.ActionType;

public abstract class AbstractAction <T extends AbstractAction<T>> implements Action<T> {

    protected final ActionType actionType;
    protected final ActionContext actionContext;

    protected AbstractAction(ActionType actionType, ActionContext actionContext) {
        this.actionType = actionType;
        this.actionContext = actionContext;
    }

    @Override
    public ActionType getActionType() {
        return actionType;
    }

    @Override
    public ActionContext getActionContext() {
        return actionContext;
    }

    @Override
    public abstract T createRunnableClone(ActionContext context);

    @Override
    public abstract void run();

}
