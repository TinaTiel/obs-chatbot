/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.factory;

import com.tinatiel.obschatbot.core.request.expand.CommandExpander;
import com.tinatiel.obschatbot.core.request.expand.CommandExpanderImpl;
import com.tinatiel.obschatbot.core.request.queue.MainQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestFactoryConfig {

    @Autowired
    MainQueue mainQueue;

    @Bean
    RequestFactory requestFactory() {
        return new RequestFactoryImpl(
                commandExpander(),
                1000,
                mainQueue
        );
    }

    @Bean
    CommandExpander commandExpander() {
        return new CommandExpanderImpl(500);
    }

}
