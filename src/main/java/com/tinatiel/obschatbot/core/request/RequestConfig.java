/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request;

import com.tinatiel.obschatbot.core.command.CommandRepository;
import com.tinatiel.obschatbot.core.messaging.*;
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
import com.tinatiel.obschatbot.core.request.scheduler.CommandRequestScheduler;
import com.tinatiel.obschatbot.core.request.scheduler.SchedulerConfig;
import com.tinatiel.obschatbot.core.request.scheduler.WorkGroupManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Import(SchedulerConfig.class)
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
        return new LinkedBlockingQueue<CommandRequest>();
    }

    @Bean
    QueueClient<CommandRequest> commandRequestQueueClient() {
        return new QueueClientImpl(commandRequestQueue());
    }

    @Bean
    QueueNotifier<CommandRequest> commandRequestQueueNotifier() {
        QueueNotifier<CommandRequest> notifier = new QueueNotifierImpl(commandRequestQueue());
        notifier.addListener(commandRequestScheduler());

        return notifier;
    }

    @Bean
    BlockingQueue<ActionRequest> actionRequestQueue() {
        return new LinkedBlockingQueue<ActionRequest>();
    }

    @Bean
    QueueClient<ActionRequest> actionRequestQueueClient() {
        return new QueueClientImpl(actionRequestQueue());
    }

    @Bean
    QueueNotifier<ActionRequest> actionRequestQueueNotifier() {
        QueueNotifier<ActionRequest> notifier = new QueueNotifierImpl(actionRequestQueue());
        // notifier.addListener(...);

        return notifier;
    }

    @Autowired
    WorkGroupManager workGroupManager;

    Listener<CommandRequest> commandRequestScheduler() {
        return new CommandRequestScheduler(workGroupManager, actionRequestQueueClient());
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
