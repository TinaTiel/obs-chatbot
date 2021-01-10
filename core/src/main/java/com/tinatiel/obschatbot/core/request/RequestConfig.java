/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request;

import com.tinatiel.obschatbot.core.client.ActionClientFactory;
import com.tinatiel.obschatbot.core.client.ActionClientFactoryImpl;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClient;
import com.tinatiel.obschatbot.core.client.obs.ObsClient;
import com.tinatiel.obschatbot.core.request.dispatch.CommandExecutorService;
import com.tinatiel.obschatbot.core.request.dispatch.CommandExecutorServiceImpl;
import com.tinatiel.obschatbot.core.request.expand.CommandExpander;
import com.tinatiel.obschatbot.core.request.expand.CommandExpanderImpl;
import com.tinatiel.obschatbot.core.request.handler.chat.ChatRequestHandler;
import com.tinatiel.obschatbot.core.request.handler.chat.ChatRequestHandlerImpl;
import com.tinatiel.obschatbot.core.request.queue.MainQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestConfig {

    @Autowired
    MainQueue mainQueue;

    @Bean
    RequestFactory requestFactory() {
        return new RequestFactoryImpl(
                commandExpander(),
//                clientFactory,
                1000,
                mainQueue
        );
    }

    @Bean
    CommandExpander commandExpander() {
        return new CommandExpanderImpl(500);
    }

}
