/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action.model;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClient;

public class SendMessageAction implements Action<TwitchChatClient, SendMessageAction> {

    private final String message;

    public SendMessageAction(String message) {
        if(message == null) throw new IllegalArgumentException("message cannot be null");
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public Class<TwitchChatClient> acceptsClientType() {
        return TwitchChatClient.class;
    }

    @Override
    public SendMessageAction clone() {
        return new SendMessageAction(message);
    }

    @Override
    public String toString() {
        return "SendMessageAction{" +
                "message='" + message + '\'' +
                '}';
    }
}
