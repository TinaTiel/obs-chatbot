/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

import com.tinatiel.obschatbot.core.infra.Listener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.*;

public class StateQueueObserverTest {

    BlockingQueue<StateEvent> stateQueue;
    Listener listener;

    StateQueueObserver consumer;

    @BeforeEach
    void setUp() {
        stateQueue = new LinkedBlockingQueue<>();
        listener = mock(Listener.class);
        consumer = new StateQueueObserver(stateQueue);
    }

    @Test
    void lastMessageSentToQueueIsCurrentState() {

        // Given several listeners are registered
        Listener<StateEvent> listener1 = mock(Listener.class);
        Listener<StateEvent> listener2 = mock(Listener.class);
        Listener<StateEvent> listener3 = mock(Listener.class);

        consumer.registerListener(listener1);
        consumer.registerListener(listener2);
        consumer.registerListener(listener3);

        // When several events are added to the queue
        stateQueue.add(new StateEvent(State.STOPPED, ""));
        stateQueue.add(new StateEvent(State.STARTING, ""));
        stateQueue.add(new StateEvent(State.CONNECTED, ""));

        // And we wait a reasonable time for eventual consistency
        try {
            Thread.sleep(20);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // Then each of the listeners will have been invoked once for each event
        verify(listener1, times(3)).onEvent(any());
        verify(listener2, times(3)).onEvent(any());
        verify(listener3, times(3)).onEvent(any());

    }
}
