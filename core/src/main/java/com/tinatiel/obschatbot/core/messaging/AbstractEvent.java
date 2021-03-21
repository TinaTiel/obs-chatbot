/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.messaging;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.UUID;

/**
 * A default Event implementation, unique by timestamp and UUID.
 * Comparison operates on timestamp, then id.
 * We use UUIDs as this would work better in distributed scenarios, and
 * we use LocalDateTime to account for location of server that is deployed.
 */
public abstract class AbstractEvent implements Event {

    private final LocalDateTime timestamp;
    private final UUID id;

    public AbstractEvent() {
        timestamp = LocalDateTime.now();
        id = UUID.randomUUID();
    }

    protected AbstractEvent(LocalDateTime timestamp, UUID id) {
        this.timestamp = timestamp;
        this.id = id;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public int compareTo(Event o) {
        return Comparator
                .comparing(Event::getTimestamp)
                .thenComparing(Event::getId)
                .compare(this, o);
    }

    @Override
    public String toString() {
        return  "timestamp=" + timestamp +
                ", id=" + id;
    }
}
