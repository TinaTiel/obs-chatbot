/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.handler.chat;

import java.util.Optional;

/**
 * Responsible for conditionally parsing a message into a command and argument parts.
 */
public interface ChatMessageParser {

  /**
   * Parse a message into a {@link ChatMessageParseResult} if the message appears to contain a
   * command request.
   */
  Optional<ChatMessageParseResult> parse(String message);
}
