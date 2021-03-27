/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClientConfig;
import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import com.tinatiel.obschatbot.core.remove.queue.QueueConfig;
import com.tinatiel.obschatbot.core.remove.queue.TwitchChatQueue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.BlockingQueue;

/**
 * This test verifies the consumer of the Twitch request queue behaves as expected given the states
 * submitted to the state queue.
 */
@Import({TwitchChatClientConfig.class, QueueConfig.class}) // todo: reorganize where queues etc are packaged
@SpringJUnitConfig
public class TwitchChatCommandRequestQueueListenerIT {

    @Autowired
    BlockingQueue<ObsChatbotEvent> twitchChatEventQueue;

    @Autowired
    TwitchChatQueue twitchChatQueue;

    @Test
    void consumeRequestsWhenReadyAndStopWhenStopping() {

        // Given the Notifier is paused

        // Given the request queue isn't empty

        // When a READY signal is placed in the state queue

        // Then the request queue has been emptied

        // And when a STOPPING signal is issued to the state queue

        // And additional requests come in

        // When we wait

        // Then we find the requests are still in the queue

        // And after a READY signal is issued again

        // Then the queue is emptied again

    }

}
