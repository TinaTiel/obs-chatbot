package com.tinatiel.obschatbot.core.client.twitch.auth;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

/**
 * Hard-coded implementation of ClientRegistrationRepository that only returns 'twitch'
 * registrations for now.
 */
public class JdbcClientRegistrationRepository implements ClientRegistrationRepository {

  private final TwitchAuthConnectionSettingsFactory twitchAuthConnectionSettingsFactory;

  public JdbcClientRegistrationRepository(
      TwitchAuthConnectionSettingsFactory twitchAuthConnectionSettingsFactory) {
    this.twitchAuthConnectionSettingsFactory = twitchAuthConnectionSettingsFactory;
  }

  @Override
  public ClientRegistration findByRegistrationId(String registrationId) {

    TwitchAuthConnectionSettings settings = twitchAuthConnectionSettingsFactory.getSettings();

    if (registrationId.equals("twitch")) {
      return ClientRegistration.withRegistrationId("twitch")
        .authorizationUri(settings.getHost() + settings.getAuthorizationPath())
        .tokenUri(settings.getHost() + settings.getTokenPath())
        .clientId(settings.getClientId())
        .clientSecret(settings.getClientSecret())
        .redirectUri(settings.getRedirectUri())
        .scope(settings.getScopes())
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
        .build();
    } else {
      return null;
    }
  }
}
