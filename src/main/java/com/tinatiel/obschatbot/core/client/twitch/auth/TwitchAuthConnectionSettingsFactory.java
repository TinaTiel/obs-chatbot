package com.tinatiel.obschatbot.core.client.twitch.auth;

import java.util.List;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@ConfigurationProperties("com.tinatiel.twitch.auth")
@Configuration
public class TwitchAuthConnectionSettingsFactory {

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
