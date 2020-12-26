package com.tinatiel.obschatbot.core.dispatch.chat;

import com.tinatiel.obschatbot.core.user.User;

public interface ChatRequestHandler {
    void handle(User user, String message);
}
