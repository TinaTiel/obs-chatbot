/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClient;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.request.queue.ActionCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RequestQueueObserverTest {

    BlockingQueue<ActionCommand> actionCommandQueue;
    RequestQueueObserver requestQueueObserver;

    @BeforeEach
    void setUp() {
        actionCommandQueue = new LinkedBlockingQueue<>();
        requestQueueObserver = new RequestQueueObserver(actionCommandQueue);
    }

    @Test
    void consumeQueueByDefault() {

        // Given some listeners registered
        Listener<ActionCommand> listener1 = mock(Listener.class);
        Listener<ActionCommand> listener2 = mock(Listener.class);
        Listener<ActionCommand> listener3 = mock(Listener.class);
        requestQueueObserver.registerListener(listener1);
        requestQueueObserver.registerListener(listener2);
        requestQueueObserver.registerListener(listener3);

        // When some tasks are added to the queue
        ActionCommand cmd1 = mock(ActionCommand.class);
        ActionCommand cmd2 = mock(ActionCommand.class);
        ActionCommand cmd3 = mock(ActionCommand.class);
        actionCommandQueue.addAll(Arrays.asList(cmd1, cmd2, cmd3));

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

    @Test
    void pausingWorksAsExpected() {

        // When we pause the observer
        requestQueueObserver.pause();

        // And register some listeners
        Listener<ActionCommand> listener1 = mock(Listener.class);
        Listener<ActionCommand> listener2 = mock(Listener.class);
        Listener<ActionCommand> listener3 = mock(Listener.class);
        requestQueueObserver.registerListener(listener1);
        requestQueueObserver.registerListener(listener2);
        requestQueueObserver.registerListener(listener3);

        // And then some tasks are added to the queue
        ActionCommand cmd1 = mock(ActionCommand.class);
        ActionCommand cmd2 = mock(ActionCommand.class);
        ActionCommand cmd3 = mock(ActionCommand.class);
        actionCommandQueue.addAll(Arrays.asList(cmd1, cmd2, cmd3));

        // And we wait a reasonable time for eventual consistency
        try {
            Thread.sleep(20);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // Then none of the listeners will have been invoked
        verify(listener1, times(0)).onEvent(any());
        verify(listener2, times(0)).onEvent(any());
        verify(listener3, times(0)).onEvent(any());

        // And then when we unpause the observer
        requestQueueObserver.resume();

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
