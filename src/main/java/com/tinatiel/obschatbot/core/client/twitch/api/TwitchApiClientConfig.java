package com.tinatiel.obschatbot.core.client.twitch.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwitchApiClientConfig {

    @Bean
    TwitchApiClient twitchApiClient() {
        return new TwitchApiClientImpl();
    }

}
