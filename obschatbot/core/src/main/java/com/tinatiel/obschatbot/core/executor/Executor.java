package com.tinatiel.obschatbot.core.executor;

import com.tinatiel.obschatbot.core.action.Action;

import java.util.List;

public interface Executor {
    void execute(List<String> arguments);
    List<Action> getActions();
}
