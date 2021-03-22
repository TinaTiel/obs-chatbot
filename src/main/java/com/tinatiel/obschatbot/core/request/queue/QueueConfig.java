/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.queue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {

    @Bean
    MainQueue mainQueue() {
        return new MainQueue();
    }

    @Bean
    ObsQueue obsQueue() {
        return new ObsQueue();
    }

    @Bean
    TwitchChatQueue twitchChatQueue() { return new TwitchChatQueue(); }

}
