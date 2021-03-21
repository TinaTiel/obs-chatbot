/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.messaging;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Describes a generic event, such as during client start/stop
 */
public interface Event extends Comparable<Event> {

    /**
     * Gets the timestamp the event was created
     */
    LocalDateTime getTimestamp();

    /**
     * Gets the unique ID for the event
     */
    UUID getId();

}
