/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

public class StateEvent {
    private final State state;
    private final String message;

    public StateEvent(State state, String message) {
        this.state = state;
        this.message = message;
    }

    public State getState() {
        return state;
    }

    public String getMessage() {
        return message;
    }
}
