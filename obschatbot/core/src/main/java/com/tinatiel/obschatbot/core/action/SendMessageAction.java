package com.tinatiel.obschatbot.core.action;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SendMessageAction implements Action {

    private final String message;

    public SendMessageAction(String message) {
        this.message = message;
    }

    @Override
    public CompletableFuture<ActionResult> execute(List<String> arguments) {
        return null;
    }

    public String getMessage() {
        return message;
    }


}
