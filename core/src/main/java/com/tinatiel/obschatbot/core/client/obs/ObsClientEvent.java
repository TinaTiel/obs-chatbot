/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.obs;

import com.tinatiel.obschatbot.core.messaging.Event;

import java.time.LocalDateTime;

public class ObsClientEvent implements Event<ObsClientState> {

    private final LocalDateTime timestamp;
    private final ObsClientState state;
    private final String message;

    public ObsClientEvent(ObsClientState state) {
        this.timestamp = LocalDateTime.now();
        this.state = state;
        this.message = null;
    }

    public ObsClientEvent(ObsClientState state, String message) {
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
