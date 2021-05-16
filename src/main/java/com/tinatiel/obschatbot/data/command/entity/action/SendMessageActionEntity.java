/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.data.command.entity.action;

import com.tinatiel.obschatbot.core.action.Action;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * An action instructing a chat client to send a message.
 */
@Getter
@Setter
@NoArgsConstructor
public class SendMessageActionEntity extends ActionEntity {

  private String message;

}
