/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.dispatch;

import com.tinatiel.obschatbot.core.action.RunnableAction;

import java.util.List;
import java.util.concurrent.Executor;

public class ExecutableCommandRequest implements Runnable {

    private final Executor executor;
    private final List<RunnableAction> actions;

    public ExecutableCommandRequest(Executor executor, List<RunnableAction> actions) {
        this.executor = executor;
        this.actions = actions;
    }

    @Override
    public void run() {

    }
}
