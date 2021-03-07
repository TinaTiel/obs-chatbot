/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.queue.consumers;

import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.core.client.obs.ObsClientManager;
import com.tinatiel.obschatbot.core.request.queue.MainQueue;
import com.tinatiel.obschatbot.core.request.queue.ObsQueue;
import com.tinatiel.obschatbot.core.request.queue.TwitchChatQueue;
import org.pircbotx.PircBotX;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ConsumerConfig {

    @Autowired
    MainQueue mainQueue;

    @Autowired
    ObsQueue obsQueue;

    @Autowired
    ObsClientManager obsClientManager;

    @Autowired
    TwitchChatQueue twitchChatQueue;

    @Autowired
    ClientManager twitchChatClientManager;

    @Bean
    MainQueueRouter mainQueueRouter() {
        return new MainQueueRouter(mainQueue, obsQueue, twitchChatQueue);
    }

    @Bean
    ObsQueueConsumer obsQueueConsumer() {
        return new ObsQueueConsumer(obsQueue, obsClientManager);
    }

    @Bean
    TwitchChatQueueConsumer twitchChatQueueConsumer() {
        return new TwitchChatQueueConsumer(twitchChatQueue, twitchChatClientManager);
    }

    @Bean
    ExecutorService queueConsumerMainExecutor() {

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.submit(mainQueueRouter());
        executorService.submit(obsQueueConsumer());
        executorService.submit(twitchChatQueueConsumer());

        return executorService;
    }

}
