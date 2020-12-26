package com.tinatiel.obschatbot.core.command;

import com.tinatiel.obschatbot.core.actionservice.ActionServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandConfig {

    @Autowired
    ActionServiceFactory actionServiceFactory;

    @Bean
    CommandRepository commandRepository() {
        return new CommandRepositoryTestImpl(actionServiceFactory);
    }

}
