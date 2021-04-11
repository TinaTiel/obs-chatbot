package com.tinatiel.obschatbot.core.user;

import com.tinatiel.obschatbot.core.client.twitch.api.TwitchApiClient;
import com.tinatiel.obschatbot.core.user.local.LocalUserConfig;
import com.tinatiel.obschatbot.core.user.local.LocalUserRepository;
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
  LocalUserRepository localUserRepository;

  @Autowired
  TwitchApiClient twitchApiClient;

  @Bean
  UserService userService() {
    return new UserServiceImpl(
      localUserRepository,
      twitchApiClient
    );
  }

}
