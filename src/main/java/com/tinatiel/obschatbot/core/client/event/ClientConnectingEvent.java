/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.event;

import com.tinatiel.obschatbot.core.messaging.AbstractObsChatbotEvent;

/**
 * Represents when a client has initiated the socket connection process with a remote server.
 */
public class ClientConnectingEvent extends AbstractObsChatbotEvent {

  public ClientConnectingEvent() {
    super();
  }

  @Override
  public String toString() {
    return "ConnectingEvent{" + super.toString() + "}";
  }
}
