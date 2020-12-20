package com.tinatiel.obschatbot.core.action;

import com.tinatiel.obschatbot.core.command.Command;

import java.util.List;

public class ExecuteCommandAction implements Action {

    private final Command command;

    public ExecuteCommandAction(Command command) {
        this.command = command;
    }

    @Override
    public void execute(List<String> arguments) {
        command.execute(arguments);
    }
}
