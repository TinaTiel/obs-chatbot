/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.event;

import com.tinatiel.obschatbot.core.messaging.AbstractObsChatbotEvent;

public class ClientErrorEvent extends AbstractObsChatbotEvent {

    private final Throwable cause;
    private final String message;

    public ClientErrorEvent(Throwable cause, String message) {
        super();
        this.cause = cause;
        this.message = message;
    }

    public Throwable getCause() {
        return cause;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ClientErrorEvent{" +
                super.toString() +
                "cause=" + cause +
                ", message='" + message + '\'' +
                '}';
    }
}