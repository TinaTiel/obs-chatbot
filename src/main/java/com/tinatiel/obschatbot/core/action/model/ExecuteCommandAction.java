/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action.model;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.client.NoOpClient;
import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.request.RequestContext;

public class ExecuteCommandAction implements Action<NoOpClient, ExecuteCommandAction> {

    private final Command target;

    public ExecuteCommandAction(Command target) {
        if(target == null) throw new IllegalArgumentException("arguments cannot be null");
        this.target = target;
    }

    @Override
    public Class<NoOpClient> acceptsClientType() {
        return NoOpClient.class;
    }

    @Override
    public ExecuteCommandAction clone() {
        return new ExecuteCommandAction(target);
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