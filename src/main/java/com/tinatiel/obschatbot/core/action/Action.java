/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action;

/**
 * Defines a specific action that can occur, for example hiding an OBS scene, sending a chat
 * message, pausing/waiting, etc.
 */
public interface Action<T extends Action<T>> {

  T clone();

  /**
   * If true, this signifies an action requires receipt of completion before it is considered
   * complete. An example of this might be a long-running operation or a wait/timer.
   */
  boolean requiresCompletion();

  /**
   * Gets the configured timeout for an action if it is blocking.
   */
  long getTimeout();

}
