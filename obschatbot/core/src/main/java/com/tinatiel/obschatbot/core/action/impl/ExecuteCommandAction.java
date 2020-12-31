/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action.impl;

import com.tinatiel.obschatbot.core.dispatch.CommandRequestContext;
import com.tinatiel.obschatbot.core.action.ActionType;
import com.tinatiel.obschatbot.core.command.Command;

public class ExecuteCommandAction extends AbstractAction<ExecuteCommandAction> {

    private final Command target;

    public ExecuteCommandAction(CommandRequestContext commandRequestContext, Command target) {
        super(ActionType.SYSTEM, commandRequestContext);
        if(target == null) throw new IllegalArgumentException("Target command is required");
        this.target = target;
    }

    public Command getTarget() {
        return target;
    }

    @Override
    public ExecuteCommandAction createRunnableClone(CommandRequestContext context) {
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
                ", actionContext=" + commandRequestContext +
                ", target= Command !" + target.getName() +
                '}';
    }
}
