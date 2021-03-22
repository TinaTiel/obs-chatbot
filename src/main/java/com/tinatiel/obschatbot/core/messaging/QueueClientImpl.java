/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.messaging;

import java.util.concurrent.BlockingQueue;

public class QueueClientImpl implements QueueClient {

    private final BlockingQueue queue;

    public QueueClientImpl(BlockingQueue queue) {
        this.queue = queue;
    }

    @Override
    public void submit(Object queueItem) {
        try{
            queue.add(queueItem);
        } catch (IllegalStateException ignoredCapacityException) {
            // do nothing
        }
    }

}
