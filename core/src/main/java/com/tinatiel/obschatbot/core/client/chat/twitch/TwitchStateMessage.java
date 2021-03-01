/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.chat.twitch;

import org.pircbotx.PircBotX;

public class TwitchStateMessage implements StateMessage<PircBotX> {
    private final PircBotX clientInstance;
    private final State state;
    private final String message;

    public TwitchStateMessage(PircBotX clientInstance, State state, String message) {
        this.clientInstance = clientInstance;
        this.state = state;
        this.message = message;
    }

    @Override
    public PircBotX getClientInstance() {
        return clientInstance;
    }

    public State getState() {
        return state;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "TwitchStateMessage{" +
                "clientInstance=" + clientInstance +
                ", state=" + state +
                ", message='" + message + '\'' +
                '}';
    }

}
