/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action.model;

import com.tinatiel.obschatbot.core.action.Action;

/**
 * An action instructing a chat client to send a message.
 */
public class SendMessageAction implements Action<SendMessageAction> {

  private final String message;

  /**
   * Construct a new instance with the specified message to send.
   */
  public SendMessageAction(String message) {
    if (message == null) {
      throw new IllegalArgumentException("message cannot be null");
    }
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public SendMessageAction clone() {
    return new SendMessageAction(message);
  }

  @Override
  public boolean requiresCompletion() {
    return false;
  }

  /**
   * This action has no timeout; always returns zero.
   */
  @Override
  public long getTimeout() {
    return 0;
  }

  @Override
  public String toString() {
    return "SendMessageAction{"
      + "message='" + message + '\''
      + '}';
  }

}
