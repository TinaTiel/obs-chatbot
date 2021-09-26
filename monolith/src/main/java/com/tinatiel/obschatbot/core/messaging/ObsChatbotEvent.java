/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.messaging;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Describes a generic event or request in this application. All events are timestamped and have a
 * unique ID to assist with serialization (should, for example, event handling be delegated to a
 * key-pair database or broker).
 */
public interface ObsChatbotEvent extends Comparable<ObsChatbotEvent> {

  /**
   * Gets the timestamp the event was created.
   */
  LocalDateTime getTimestamp();

  /**
   * Gets the unique ID for the event.
   */
  UUID getId();

}
