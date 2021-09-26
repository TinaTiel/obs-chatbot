package com.tinatiel.obschatbot.data.load;

import com.tinatiel.obschatbot.data.client.obs.ObsClientDataService;
import com.tinatiel.obschatbot.data.client.twitch.auth.TwitchClientAuthDataService;
import com.tinatiel.obschatbot.data.client.twitch.chat.TwitchClientChatDataService;
import com.tinatiel.obschatbot.data.system.SystemSettingsDataService;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoaderConfig {

  @Autowired
  OwnerService ownerService;
  @Autowired
  ObsClientDataService obsClientDataService;
  @Autowired
  TwitchClientChatDataService twitchClientChatDataService;
  @Autowired
  TwitchClientAuthDataService twitchClientAuthDataService;
  @Autowired
  SystemSettingsDataService systemSettingsDataService;

  @Bean
  DataLoader dataLoader() {
    return new DataLoaderImpl(
      ownerService,
      obsClientDataService,
      twitchClientChatDataService,
      twitchClientAuthDataService,
      systemSettingsDataService
    );
  }

  @Bean
  DataLoaderListener dataLoaderListener() {
    return new DataLoaderListener(dataLoader());
  }

}
