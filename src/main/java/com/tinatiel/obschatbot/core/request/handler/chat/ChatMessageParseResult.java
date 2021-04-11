/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.handler.chat;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents the result of parsing a String chat message into its respective parts, the (1) command
 * name that was invoked, and (2) the proceeding arguments to that command.
 */
@AllArgsConstructor
@Getter
@ToString
public class ChatMessageParseResult {

  private final String commandName;
  private final List<String> args;

}
