/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.messaging;

import java.time.LocalDateTime;

/**
 * Describes a state event, such as during client start/stop
 * @param <T> An enum describing the event, allows for type-safe comparison in Listeners.
 */
public interface StateEvent<T extends Enum> {

    /**
     * Gets the timestamp the event was created
     */
    LocalDateTime getTimestamp();

    /**
     * Gets the state of the event
     */
    T getState();

    /**
     * Gets the optional message associated with the event. Generally
     * this should only be used for exception events
     */
    String getMessage();
}
