package com.tinatiel.obschatbot.core.actionservice.twitch;

public class FooServiceImpl implements FooService {
    @Override
    public String processMessage(String message) {
        return "Got your message, it was: " + message;
    }
}
