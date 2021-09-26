package com.tinatiel.obschatbot.data.client.twitch.chat;

import com.tinatiel.obschatbot.data.client.twitch.chat.entity.TwitchClientChatDataRepository;
import com.tinatiel.obschatbot.data.client.twitch.chat.mapper.TwitchClientChatDataMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EntityScan("com.tinatiel.obschatbot.data.client.twitch.chat.entity")
@Configuration
public class TwitchClientChatDataConfig {

  @Autowired
  TwitchClientChatDataRepository twitchClientChatDataRepository;

  @Bean
  TwitchClientChatDataMapper twitchClientChatDataMapper() {
    return Mappers.getMapper(TwitchClientChatDataMapper.class);
  }

  @Bean
  TwitchClientChatDataService twitchClientChatDataService() {
    return new TwitchClientChatDataServiceImpl(twitchClientChatDataRepository, twitchClientChatDataMapper());
  }
  
}
