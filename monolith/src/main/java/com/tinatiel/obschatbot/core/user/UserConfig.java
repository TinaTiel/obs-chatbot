package com.tinatiel.obschatbot.core.user;

import com.tinatiel.obschatbot.core.client.twitch.api.TwitchApiClient;
import com.tinatiel.obschatbot.data.localuser.LocalUserConfig;
import com.tinatiel.obschatbot.data.localuser.LocalUserService;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Encompasses all configuration for the UserService.
 */
@Import(LocalUserConfig.class)
@Configuration
public class UserConfig {

  @Autowired
  OwnerService ownerService;

  @Autowired
  LocalUserService localUserService;

  @Autowired
  TwitchApiClient twitchApiClient;

  @Bean
  UserService userService() {
    return new UserServiceImpl(
      ownerService, localUserService,
      twitchApiClient
    );
  }

}
