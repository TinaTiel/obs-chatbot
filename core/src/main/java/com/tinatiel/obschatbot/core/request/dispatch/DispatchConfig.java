/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.dispatch;

import com.tinatiel.obschatbot.core.request.RequestFactory;
import com.tinatiel.obschatbot.core.request.queue.MainQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DispatchConfig {

    @Autowired
    RequestFactory requestFactory;

    @Bean
    CommandDispatcher commandDispatcher() {
        return new CommandDispatcherImpl(
                requestFactory,
                commandExecutorService()
        );
    }

    @Bean
    CommandExecutorService commandExecutorService() {
        return new CommandExecutorServiceImpl(
                -1,
                25
        );
    }

}
