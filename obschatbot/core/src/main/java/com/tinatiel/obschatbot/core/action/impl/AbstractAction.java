/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action.impl;

import com.tinatiel.obschatbot.core.action.RunnableAction;
import com.tinatiel.obschatbot.core.dispatch.CommandRequestContext;
import com.tinatiel.obschatbot.core.action.ActionType;

public abstract class AbstractAction <T extends AbstractAction<T>> implements RunnableAction<T> {

    protected final ActionType actionType;
    protected final CommandRequestContext commandRequestContext;

    protected AbstractAction(ActionType actionType, CommandRequestContext commandRequestContext) {
        this.actionType = actionType;
        this.commandRequestContext = commandRequestContext;
    }

    @Override
    public ActionType getActionType() {
        return actionType;
    }

    @Override
    public CommandRequestContext getRequestContext() {
        return commandRequestContext;
    }

    @Override
    public abstract T createRunnableClone(CommandRequestContext context);

    @Override
    public abstract void run();

}
