/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.queue.consumers;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClient;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClientManager;
import com.tinatiel.obschatbot.core.client.obs.ObsClient;
import com.tinatiel.obschatbot.core.client.obs.ObsClientManager;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.request.queue.ActionCommand;
import com.tinatiel.obschatbot.core.request.queue.ObsQueue;
import com.tinatiel.obschatbot.core.request.queue.TwitchChatQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TwitchChatQueueConsumerTest {

    TwitchChatQueue twitchQueue;
    TwitchChatClientManager twitchClientManager;

    TwitchChatQueueConsumer consumer;

    @BeforeEach
    void setUp() {
        twitchQueue = new TwitchChatQueue();
        twitchClientManager = mock(TwitchChatClientManager.class);

        consumer = new TwitchChatQueueConsumer(twitchQueue, twitchClientManager);
    }

    @Test
    void consumesContentsOfQueue() {

        // Given some OBS tasks in the queue
        ActionCommand twitchCmd1 = new ActionCommand(TwitchChatClient.class, mock(Action.class), mock(RequestContext.class));
        ActionCommand twitchCmd2 = new ActionCommand(TwitchChatClient.class, mock(Action.class), mock(RequestContext.class));
        ActionCommand twitchCmd3 = new ActionCommand(TwitchChatClient.class, mock(Action.class), mock(RequestContext.class));
        twitchQueue.add(twitchCmd1);
        twitchQueue.add(twitchCmd2);
        twitchQueue.add(twitchCmd3);

        // When run
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(consumer);
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.MILLISECONDS);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // Then the client manager was invoked three times
        verify(twitchClientManager, times(3)).consume(any(ActionCommand.class));

    }

}
