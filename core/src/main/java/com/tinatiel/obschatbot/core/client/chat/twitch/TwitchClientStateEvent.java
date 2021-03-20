/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.chat.twitch;

import com.tinatiel.obschatbot.core.messaging.StateEvent;

import java.time.LocalDateTime;

public class TwitchClientStateEvent implements StateEvent<TwitchClientState> {

    private final LocalDateTime timestamp;
    private final TwitchClientState state;
    private final String message;

    public TwitchClientStateEvent(TwitchClientState state) {
        this.timestamp = LocalDateTime.now();
        this.state = state;
        this.message = null;
    }

    public TwitchClientStateEvent(TwitchClientState state, String message) {
        this.timestamp = LocalDateTime.now();
        this.state = state;
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public TwitchClientState getState() {
        return state;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "TwitchClientStateEvent{" +
                "timestamp=" + timestamp +
                ", state=" + state +
                ", message='" + message + '\'' +
                '}';
    }
}
