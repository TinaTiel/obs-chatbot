/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ClientManagerStateConsumerTest {

    BlockingQueue<StateMessage> stateQueue;
    Listener listener;

    ClientManagerStateConsumer consumer;

    @BeforeEach
    void setUp() {
        stateQueue = new LinkedBlockingQueue<>();
        listener = mock(Listener.class);
        consumer = new ClientManagerStateConsumer(stateQueue, listener);
    }

    @Test
    void lastMessageSentToQueueIsCurrentState() {

        // Given a queue with several messages
        stateQueue.add(new StateMessage(State.STOPPED, ""));
        stateQueue.add(new StateMessage(State.STARTING, ""));
        stateQueue.add(new StateMessage(State.CONNECTED, ""));

        // When we wait reasonable time for eventual consistency
        try {
            Thread.sleep(20);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        State currentState = consumer.getState();

        // Then it is eventually-consistent; it reflects the last message in the queue
        assertThat(currentState).isEqualTo(State.CONNECTED);
        verify(listener, times(3)).onState(any(), any());

    }
}
