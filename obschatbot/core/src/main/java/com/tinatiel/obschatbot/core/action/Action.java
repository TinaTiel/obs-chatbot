package com.tinatiel.obschatbot.core.action;

import com.tinatiel.obschatbot.core.dispatch.CommandRequest;

public interface Action<T extends Action<T>> extends Runnable {
    ActionType getActionType();
    CommandRequest getActionContext();
    T createRunnableClone(CommandRequest context);
}
