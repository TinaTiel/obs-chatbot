/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

import com.tinatiel.obschatbot.core.client.obs.ObsClient;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActionClientConfig {

    @Autowired
    ObsClient obsClient;

    @Autowired
    TwitchChatClient twitchChatClient;

    @Bean
    ActionClientFactory clientFactory() {

        return new ActionClientFactoryImpl(
                obsClient,
                twitchChatClient
        );

    }

}
