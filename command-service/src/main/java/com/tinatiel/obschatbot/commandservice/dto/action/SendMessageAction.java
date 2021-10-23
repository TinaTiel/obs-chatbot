/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.commandservice.dto.action;

import com.tinatiel.obschatbot.commandservice.dto.CommandArgs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * An action instructing a chat client to send a message.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
public class SendMessageAction implements Action {

  private String message;

  @Override
  public Action withCommandArgs(CommandArgs commandArgs) {
    // TODO map any args by name into message template; e.g. if the message is "Hello {username}"
    // and the args includes an argument "username" with value 'Steve' then the copy of the action
    // should have the message "Hello Steve"
    return this;
  }
}
