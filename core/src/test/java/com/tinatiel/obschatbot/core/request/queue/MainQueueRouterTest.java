/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.queue;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.client.ActionClient;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClient;
import com.tinatiel.obschatbot.core.client.obs.ObsClient;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.request.queue.consumers.MainQueueRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.mock;

public class MainQueueRouterTest {

    MainQueue mainQueue;
    ObsQueue obsQueue;
    TwitchChatQueue twitchChatQueue;

    MainQueueRouter router;

    @BeforeEach
    void setUp() {
        mainQueue = new MainQueue();
        obsQueue = new ObsQueue();
        twitchChatQueue = new TwitchChatQueue();

        router = new MainQueueRouter(mainQueue, obsQueue, twitchChatQueue);
    }

    @Test
    void requestsRoutedAsExpected() {

        // Given types of ActionCommands
        ActionCommand obsAction = new ActionCommand(ObsClient.class, mock(Action.class), mock(RequestContext.class));
        ActionCommand twitchAction = new ActionCommand(TwitchChatClient.class, mock(Action.class), mock(RequestContext.class));
        ActionCommand unknownAction = new ActionCommand(UnknownClient.class, mock(Action.class), mock(RequestContext.class));

        // And given those commands are added to the main queue
        mainQueue.add(unknownAction);
        mainQueue.add(obsAction);
        mainQueue.add(twitchAction);

        // When we run the router and let it finish
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(router);
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.MILLISECONDS);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // Then they are transferred to the correct queues (noting that the unknown action was simply dropped)
        assertThat(obsQueue).contains(obsAction);
        assertThat(twitchChatQueue).contains(twitchAction);
        assertThat(mainQueue.toArray()).isEmpty();

    }

    private static interface UnknownClient extends ActionClient {

    }
}
