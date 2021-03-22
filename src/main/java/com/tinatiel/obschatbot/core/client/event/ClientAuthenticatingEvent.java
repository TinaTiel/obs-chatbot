/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.event;

import com.tinatiel.obschatbot.core.messaging.AbstractObsChatbotEvent;

public class ClientAuthenticatingEvent extends AbstractObsChatbotEvent {
    public ClientAuthenticatingEvent() {
        super();
    }

    @Override
    public String toString() {
        return "AuthenticatingEvent{" + super.toString() + "}";
    }
}
