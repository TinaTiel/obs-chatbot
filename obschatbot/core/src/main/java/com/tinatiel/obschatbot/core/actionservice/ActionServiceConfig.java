package com.tinatiel.obschatbot.core.actionservice;

import com.tinatiel.obschatbot.core.actionservice.obs.ObsClient;
import com.tinatiel.obschatbot.core.actionservice.twitch.TwitchChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActionServiceConfig {

    @Autowired
    ObsClient obsClient;

    //@Autowired
    TwitchChatClient twitchChatClient;

    @Bean
    ActionServiceFactory actionServiceFactory() {

        return new ActionServiceFactoryImpl(
                obsClient,
                twitchChatClient
        );

    }

}
