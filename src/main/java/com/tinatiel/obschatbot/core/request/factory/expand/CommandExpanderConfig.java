package com.tinatiel.obschatbot.core.request.factory.expand;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandExpanderConfig {
    @Bean
    CommandExpander commandExpander() {
        return new CommandExpanderImpl(500);
    }
}
