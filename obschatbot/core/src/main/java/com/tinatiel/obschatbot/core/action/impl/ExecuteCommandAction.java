package com.tinatiel.obschatbot.core.action.impl;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.command.Command;

public class ExecuteCommandAction implements Action {

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
}
