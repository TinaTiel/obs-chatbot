package com.tinatiel.obschatbot.core.action.impl;

import com.tinatiel.obschatbot.core.action.Action;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SendMessageAction implements Action {

    private final String message;

    public SendMessageAction(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void run() {

    }
}
