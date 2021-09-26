package com.tinatiel.obschatbot.core.client.twitch.api;

import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * An implementation of ClientSettingsFactory returning instances of settings required for the
 * TwitchApiClient.
 */
@Setter
@ConfigurationProperties("com.tinatiel.twitch.api")
@Configuration
public class TwitchApiClientSettingsFactory implements
    ClientSettingsFactory<TwitchApiClientSettings> {

  private String host;
  private String usersFollowsPath;
  private String usersPath;

  @Override
  public TwitchApiClientSettings getSettings() {
    return TwitchApiClientSettings.builder()
      .host(host)
      .usersFollowsPath(usersFollowsPath)
      .usersPath(usersPath)
      .build();
  }
}
