package com.tinatiel.obschatbot.core.client.twitch.api;

import com.tinatiel.obschatbot.security.owner.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

/**
 * Configures the Twitch Api Client.
 */
@Configuration
public class TwitchApiClientConfig {

  @Autowired
  OAuth2AuthorizedClientService authorizedClientService;

  @Autowired
  TwitchApiClientSettingsFactory twitchApiClientSettingsFactory;

  @Autowired
  OwnerService ownerService;

  @Bean
  TwitchApiClient twitchApiClient() {

    return new TwitchApiClientImpl(
      ownerService, authorizedClientService,
      twitchApiClientSettingsFactory
    );
  }

}
