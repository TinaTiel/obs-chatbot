package com.tinatiel.obschatbot.data.client.twitch;

import com.tinatiel.obschatbot.data.client.twitch.entity.TwitchClientDataRepository;
import com.tinatiel.obschatbot.data.client.twitch.mapper.TwitchClientDataMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EntityScan("com.tinatiel.obschatbot.data.client.twitch.entity")
@Configuration
public class TwitchClientDataConfig {

  @Autowired
  TwitchClientDataRepository twitchClientDataRepository;

  @Bean
  TwitchClientDataMapper twitchClientDataMapper() {
    return Mappers.getMapper(TwitchClientDataMapper.class);
  }

  @Bean
  TwitchClientDataService twitchClientDataService() {
    return new TwitchClientDataServiceImpl(twitchClientDataRepository, twitchClientDataMapper());
  }
  
}
