/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.event;

import com.tinatiel.obschatbot.core.messaging.AbstractObsChatbotEvent;

/**
 * Represents an irrecoverable error condition at a client that should result in stopping the client
 * and attempting to start it only after addressing the underlying problem. Examples include auth
 * failure, inaccessible networks, revoked tokens, etc.
 */
public class ClientErrorEvent extends AbstractObsChatbotEvent {

  private final Throwable cause;
  private final String message;

  /**
   * Create a new ClientErrorEvent instance. If an exception was thrown, it should be supplied
   * otherwise it can be null. Message should always be supplied, ideally in a language friendly
   * to non-devs as these messages may be shown directly to users.
   */
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
    return "ClientErrorEvent{"
      + super.toString()
      + "cause=" + cause
      + ", message='" + message + '\''
      + '}';
  }
}
