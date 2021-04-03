/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.command;

import com.tinatiel.obschatbot.core.request.factory.expand.CommandExpanderConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Optional;

@Import(CommandExpanderConfig.class)
@Configuration
public class CommandConfig {

    @Bean
    public CommandRepository commandRepository() {
        return new CommandRepository() {
            @Override
            public Optional<Command> findByName(String name) {
                return Optional.empty();
            }
        };
    }

}
