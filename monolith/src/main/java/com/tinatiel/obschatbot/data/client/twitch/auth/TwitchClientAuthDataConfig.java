package com.tinatiel.obschatbot.data.client.twitch.auth;

import com.tinatiel.obschatbot.data.client.twitch.auth.entity.TwitchClientAuthDataRepository;
import com.tinatiel.obschatbot.data.client.twitch.auth.mapper.TwitchClientAuthDataMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EntityScan("com.tinatiel.obschatbot.data.client.twitch.auth.entity")
@Configuration
public class TwitchClientAuthDataConfig {

  @Autowired
  TwitchClientAuthDataRepository twitchClientAuthDataRepository;

  @Bean
  TwitchClientAuthDataMapper twitchClientAuthDataMapper() {
    return Mappers.getMapper(TwitchClientAuthDataMapper.class);
  }

  @Bean
  TwitchClientAuthDataService twitchClientAuthDataService() {
    return new TwitchClientAuthDataServiceImpl(twitchClientAuthDataRepository, twitchClientAuthDataMapper());
  }
  
}
