package com.tinatiel.obschatbot.core.client.twitch.auth;

import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;
import com.tinatiel.obschatbot.data.client.twitch.auth.TwitchClientAuthDataService;
import com.tinatiel.obschatbot.security.owner.OwnerDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import java.util.List;
import lombok.AccessLevel;
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

  @Setter(AccessLevel.NONE)
  private final TwitchClientAuthDataService twitchClientAuthDataService;
  @Setter(AccessLevel.NONE)
  private final OwnerService ownerService;

  public TwitchAuthConnectionSettingsFactory(
    TwitchClientAuthDataService twitchClientAuthDataService,
    OwnerService ownerService) {
    this.twitchClientAuthDataService = twitchClientAuthDataService;
    this.ownerService = ownerService;
  }

  @Override
  public TwitchAuthConnectionSettings getSettings() {
    TwitchAuthConnectionSettings.TwitchAuthConnectionSettingsBuilder builder =
        TwitchAuthConnectionSettings.builder()
        .host(host)
        .authorizationPath(authorizationPath)
        .tokenPath(tokenPath)
        .validationPath(validationPath)
        .scopes(scopes)
        .clientId(clientId)
        .clientSecret(clientSecret)
        .redirectUri(redirectUri);

    // If client id and secret were not provided in the environment, get them from the database
    if(clientId == null || clientSecret == null) {
      OwnerDto owner = ownerService.getOwner();
      if(owner != null) {
        twitchClientAuthDataService.findByOwner(owner.getId()).ifPresentOrElse(it -> {
          builder.clientId(it.getClientId());
          builder.clientSecret(it.getClientSecret());
        }, () -> {
          throw new IllegalArgumentException("No Twitch Client settings could be found for owner " + owner);
        });
      }
    }

    return builder.build();
  }

}
