package com.tinatiel.obschatbot.data.client.twitch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwitchClientDataConfig {

  @Bean
  TwitchClientDataService twitchClientDataService() {
    return new TwitchClientDataServiceImpl();
  }
  
}
