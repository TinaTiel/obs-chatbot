/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request;

import com.tinatiel.obschatbot.core.command.CommandRepository;
import com.tinatiel.obschatbot.core.messaging.QueueClient;
import com.tinatiel.obschatbot.core.messaging.QueueClientImpl;
import com.tinatiel.obschatbot.core.messaging.QueueNotifier;
import com.tinatiel.obschatbot.core.messaging.QueueNotifierImpl;
import com.tinatiel.obschatbot.core.request.handler.CommandRequestDispatcher;
import com.tinatiel.obschatbot.core.request.expand.CommandExpander;
import com.tinatiel.obschatbot.core.request.expand.CommandExpanderImpl;
import com.tinatiel.obschatbot.core.request.factory.RequestFactory;
import com.tinatiel.obschatbot.core.request.factory.RequestFactoryImpl;
import com.tinatiel.obschatbot.core.request.handler.CommandRequestDispatcherImpl;
import com.tinatiel.obschatbot.core.request.handler.chat.ChatMessageParser;
import com.tinatiel.obschatbot.core.request.handler.chat.ChatMessageParserImpl;
import com.tinatiel.obschatbot.core.request.handler.chat.ChatRequestHandler;
import com.tinatiel.obschatbot.core.request.handler.chat.ChatRequestHandlerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class RequestConfig {

    @Autowired
    CommandRepository commandRepository;

    @Bean
    CommandExpander commandExpander() {
        return new CommandExpanderImpl(500);
    }

    @Bean
    RequestFactory requestFactory() {
        return new RequestFactoryImpl(
                commandExpander()
        );
    }

    @Bean
    BlockingQueue<CommandRequest> commandRequestQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean
    QueueClient<CommandRequest> commandRequestQueueClient() {
        return new QueueClientImpl(commandRequestQueue());
    }

    @Bean
    QueueNotifier<CommandRequest> commandRequestQueueNotifier() {
        QueueNotifier<CommandRequest> notifier = new QueueNotifierImpl(commandRequestQueue());
        // notifier.addListener(...);

        return notifier;
    }

    @Bean
    CommandRequestDispatcher commandRequestDispatcher() {
        return new CommandRequestDispatcherImpl(
                requestFactory(),
                commandRequestQueueClient()
        );
    }

    @Bean
    ChatRequestHandler chatRequestHandler() {
        return new ChatRequestHandlerImpl(
                chatMessageParser(),
                commandRepository,
                commandRequestDispatcher()
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
