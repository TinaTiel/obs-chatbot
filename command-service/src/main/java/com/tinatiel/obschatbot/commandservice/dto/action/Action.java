/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.commandservice.dto.action;

import com.tinatiel.obschatbot.commandservice.dto.CommandArgs;

/**
 * Defines a specific action that can occur, for example hiding an OBS scene, sending a chat
 * message, pausing/waiting, etc.
 */
public interface Action {

  /**
   * Creates a clone of the Action using the supplied args
   */
  Action withCommandArgs(CommandArgs commandArgs);
}
