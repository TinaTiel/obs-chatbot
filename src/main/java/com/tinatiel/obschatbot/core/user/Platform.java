/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.user;

/**
 * Represents a platform supported by the application.
 */
public enum Platform {
  /**
   * Local/Test platforms, used for local command-line invocation or locally via the REST API.
   *
   * @see User#systemUser()
   */
  LOCAL,

  /**
   * Twitch platform (https://www.twitch.tv/).
   */
  TWITCH
}
