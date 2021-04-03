package com.tinatiel.obschatbot.core.request.factory;

import com.tinatiel.obschatbot.core.request.factory.expand.CommandExpander;
import com.tinatiel.obschatbot.core.request.factory.expand.CommandExpanderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(CommandExpanderConfig.class)
@Configuration
public class RequestFactoryConfig {

    @Autowired
    CommandExpander commandExpander;

    @Bean
    RequestFactory requestFactory() {
        return new RequestFactoryImpl(
                commandExpander
        );
    }
}
