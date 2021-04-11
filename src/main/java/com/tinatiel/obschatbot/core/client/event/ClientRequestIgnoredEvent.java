/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.event;

import com.tinatiel.obschatbot.core.messaging.AbstractObsChatbotEvent;

/**
 * When a request should be ignored (for example, requesting a running client to run again), emit
 * this event rather than silently dropping the request. Not only is this friendly to the end-user
 * but it is important in tests for acknowledging a request and has been received (but is being
 * ignored for various reasons).
 */
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
    return "ClientRequestIgnoredEvent{"
      + super.toString()
      + "reason='" + reason + '\''
      + '}';
  }
}
