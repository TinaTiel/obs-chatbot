package com.tinatiel.obschatbot.core.action.impl;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.ActionContext;
import com.tinatiel.obschatbot.core.action.ActionType;
import com.tinatiel.obschatbot.core.command.Command;

public class ExecuteCommandAction extends AbstractAction<ExecuteCommandAction> {

    private final Command target;

    public ExecuteCommandAction(ActionContext actionContext, Command target) {
        super(ActionType.SYSTEM, actionContext);
        if(target == null) throw new IllegalArgumentException("Target command is required");
        this.target = target;
    }

    public Command getTarget() {
        return target;
    }

    @Override
    public ExecuteCommandAction createRunnableClone(ActionContext context) {
        return new ExecuteCommandAction(context, target);
    }

    @Override
    public void run() {
        // Do nothing
    }

    @Override
    public String toString() {
        return "ExecuteCommandAction{" +
                "actionType=" + actionType +
                ", actionContext=" + actionContext +
                ", target= Command !" + target.getName() +
                '}';
    }
}
