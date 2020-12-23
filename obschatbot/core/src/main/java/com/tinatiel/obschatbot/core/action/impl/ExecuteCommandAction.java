package com.tinatiel.obschatbot.core.action.impl;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.ActionContext;
import com.tinatiel.obschatbot.core.action.ActionType;
import com.tinatiel.obschatbot.core.command.Command;

public class ExecuteCommandAction implements Action<ExecuteCommandAction> {

    private final Command target;

    public ExecuteCommandAction(Command target) {
        if(target == null) throw new IllegalArgumentException("Target command is required");
        this.target = target;
    }

    public Command getTarget() {
        return target;
    }

    @Override
    public void run() {
        // Do nothing
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
    public ExecuteCommandAction createRunnableClone(ExecuteCommandAction action, ActionContext context) {
        return null;
    }
}
