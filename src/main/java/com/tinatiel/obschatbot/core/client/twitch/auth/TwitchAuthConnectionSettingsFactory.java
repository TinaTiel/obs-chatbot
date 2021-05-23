package com.tinatiel.obschatbot.core.client.twitch.auth;

import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;
import java.util.List;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Implementation of ClientSettingsFactory providing access to settings required to initialize
 * the OAuth client required to communicate with Twitch's APIs.
 */
@Setter
  @ConfigurationProperties("com.tinatiel.twitch.auth")
@Configuration
public class TwitchAuthConnectionSettingsFactory implements
    ClientSettingsFactory<TwitchAuthConnectionSettings> {

  private String host;
  private String authorizationPath;
  private String tokenPath;
  private String validationPath;
  private List<String> scopes;
  private String clientId;
  private String clientSecret;
  private String redirectUri;

  public TwitchAuthConnectionSettingsFactory() {

  }

  @Override
  public TwitchAuthConnectionSettings getSettings() {
    return TwitchAuthConnectionSettings.builder()
      .host(host)
      .authorizationPath(authorizationPath)
      .tokenPath(tokenPath)
      .validationPath(validationPath)
      .scopes(scopes)
      .clientId(clientId)
      .clientSecret(clientSecret)
      .redirectUri(redirectUri)
      .build();
  }

}
