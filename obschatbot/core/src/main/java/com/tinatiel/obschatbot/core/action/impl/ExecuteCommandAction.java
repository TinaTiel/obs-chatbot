package com.tinatiel.obschatbot.core.action.impl;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.command.Command;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ExecuteCommandAction implements Action {

    private final Command command;

    public ExecuteCommandAction(Command command) {
        this.command = command;
    }

    @Override
    public void run() {

    }
}
