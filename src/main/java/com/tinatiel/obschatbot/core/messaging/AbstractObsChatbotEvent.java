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
public abstract class AbstractObsChatbotEvent implements ObsChatbotEvent {

    private final LocalDateTime timestamp;
    private final UUID id;

    public AbstractObsChatbotEvent() {
        timestamp = LocalDateTime.now();
        id = UUID.randomUUID();
    }

    protected AbstractObsChatbotEvent(LocalDateTime timestamp, UUID id) {
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
    public int compareTo(ObsChatbotEvent o) {
        return Comparator
                .comparing(ObsChatbotEvent::getTimestamp)
                .thenComparing(ObsChatbotEvent::getId)
                .compare(this, o);
    }

    @Override
    public String toString() {
        return  "timestamp=" + timestamp +
                ", id=" + id + ", ";
    }
}
