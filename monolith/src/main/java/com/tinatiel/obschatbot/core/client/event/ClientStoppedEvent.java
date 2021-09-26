/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.event;

import com.tinatiel.obschatbot.core.messaging.AbstractObsChatbotEvent;

/**
 * This event should be emitted ONLY when the client has fully disconnected from the remote server
 * and is therefore fully stopped.
 */
public class ClientStoppedEvent extends AbstractObsChatbotEvent {

  public ClientStoppedEvent() {
    super();
  }

  @Override
  public String toString() {
    return "StoppedEvent{" + super.toString() + "}";
  }
}
