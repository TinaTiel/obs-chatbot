/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.obs;

import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchClientState;
import com.tinatiel.obschatbot.core.messaging.StateEvent;

import java.time.LocalDateTime;

public class ObsClientStateEvent implements StateEvent<ObsClientState> {

    private final LocalDateTime timestamp;
    private final ObsClientState state;
    private final String message;

    public ObsClientStateEvent(ObsClientState state) {
        this.timestamp = LocalDateTime.now();
        this.state = state;
        this.message = null;
    }

    public ObsClientStateEvent(ObsClientState state, String message) {
        this.timestamp = LocalDateTime.now();
        this.state = state;
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public ObsClientState getState() {
        return state;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ObsClientStateEvent{" +
                "timestamp=" + timestamp +
                ", state=" + state +
                ", message='" + message + '\'' +
                '}';
    }
}
