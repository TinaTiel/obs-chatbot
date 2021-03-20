/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;

public class QueueClientTest {

    @Test
    void submitToQueue() {

        // Given an unbounded empty queue
        BlockingQueue queue = new LinkedBlockingQueue();
        QueueClient client = new QueueClientImpl(queue);

        // When items added sent by the client
        client.submit(new Object());
        client.submit(new Object());
        client.submit(new Object());

        // Then they are in the queue
        assertThat(queue).hasSize(3);

    }

    @Test
    void submitToFullQueueDropsRequest() throws InterruptedException {

        // Given two different queue items
        Object existingItem = new Object();
        Object newItem = new Object();

        assertThat(existingItem).isNotEqualTo(newItem);

        // Given a bounded full queue
        BlockingQueue queue = new LinkedBlockingQueue(1);
        queue.add(existingItem);
        QueueClient client = new QueueClientImpl(queue);

        // When items are submitted
        client.submit(newItem);

        // Then the new item is dropped
        assertThat(queue).hasSize(1);
        assertThat(queue.take()).isEqualTo(existingItem);

    }

}
