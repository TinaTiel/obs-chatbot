/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action.model;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.ActionType;
import com.tinatiel.obschatbot.core.action.RunnableAction;
import com.tinatiel.obschatbot.core.client.ActionClient;
import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.dispatch.CommandRequestContext;

public class ExecuteCommandAction implements Action<ExecuteCommandAction> {

    private final ActionType ACTION_TYPE = ActionType.SYSTEM;
    private final Command target;

    public ExecuteCommandAction(Command target) {
        this.target = target;
    }

    @Override
    public ActionType getActionType() {
        return ACTION_TYPE;
    }

    @Override
    public ExecuteCommandAction clone() {
        return new ExecuteCommandAction(target);
    }

    @Override
    public RunnableAction<ExecuteCommandAction> createRunnableAction(ActionClient client, CommandRequestContext commandRequestContext) {
        return null;
    }

    public Command getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "ExecuteCommandAction{" +
                ", target=!" + target.getName() +
                '}';
    }
}
