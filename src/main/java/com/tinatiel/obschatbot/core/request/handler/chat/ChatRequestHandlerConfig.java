/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.handler.chat;

import com.tinatiel.obschatbot.core.command.CommandRepository;
import com.tinatiel.obschatbot.core.request.dispatch.CommandDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatRequestHandlerConfig {

    @Autowired
    CommandRepository commandRepository;

    @Autowired
    CommandDispatcher commandDispatcher;

    @Bean
    ChatRequestHandler chatRequestHandler() {
        return new ChatRequestHandlerImpl(
                chatMessageParser(),
                commandRepository,
                commandDispatcher
        );
    }

    @Bean
    ChatMessageParser chatMessageParser() {
        return new ChatMessageParserImpl(
                "!",
                true
        );
    }
}
