/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.event;

import com.tinatiel.obschatbot.core.messaging.AbstractObsChatbotEvent;

public class ClientRequestIgnoredEvent extends AbstractObsChatbotEvent {
    private final String reason;

    public ClientRequestIgnoredEvent(String reason) {
        super();
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "IgnoredEvent{" +
                super.toString() +
                "reason='" + reason + '\'' +
                '}';
    }
}
