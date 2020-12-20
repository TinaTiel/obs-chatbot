package com.tinatiel.obschatbot.core.action;

import java.util.List;

public class SendMessageAction implements Action {

    private final String message;

    public SendMessageAction(String message) {
        this.message = message;
    }

    @Override
    public void execute(List<String> arguments) {

    }

    public String getMessage() {
        return message;
    }


}
