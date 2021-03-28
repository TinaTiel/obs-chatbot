/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.remove.queue.consumers;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.remove.queue.TwitchChatQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Disabled
public class TwitchChatQueueConsumerTest {

    TwitchChatQueue twitchQueue;
    ClientManager twitchClientManager;

    TwitchChatQueueConsumer consumer;

    @BeforeEach
    void setUp() {
        twitchQueue = new TwitchChatQueue();
        twitchClientManager = mock(ClientManager.class);

        consumer = new TwitchChatQueueConsumer(twitchQueue, twitchClientManager);
    }

    @Test
    void consumesContentsOfQueue() {

        // Given some OBS tasks in the queue
        ActionRequest twitchCmd1 = new ActionRequest(mock(RequestContext.class), mock(Action.class));
        ActionRequest twitchCmd2 = new ActionRequest(mock(RequestContext.class), mock(Action.class));
        ActionRequest twitchCmd3 = new ActionRequest(mock(RequestContext.class), mock(Action.class));
        twitchQueue.add(twitchCmd1);
        twitchQueue.add(twitchCmd2);
        twitchQueue.add(twitchCmd3);

        // When run
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(consumer);
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.MILLISECONDS);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // Then the client manager was invoked three times, in order
        verify(twitchClientManager, times(3)).consume(any(ActionRequest.class));

    }

}
