package com.tinatiel.obschatbot.core.client.twitch.auth;

import com.tinatiel.obschatbot.security.SystemPrincipalOauth2AuthorizedClientRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

/**
 * Encompasses all settings required to authenticate with Twitch.
 */

@EnableScheduling
@Slf4j
@Configuration
public class TwitchOauth2ClientConfig {

  @Autowired
  TwitchAuthConnectionSettingsFactory twitchAuthConnectionSettingsFactory;

  /**
   * manages **authorized** clients TODO Replace with JdbcOAuth2AuthorizedClientService
   * https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/oauth2/client/JdbcOAuth2AuthorizedClientService.html
   */
  @Bean
  OAuth2AuthorizedClientService authorizedClientService() {
    return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
  }

  /**
   * This is responsible for noting that an authorization request was made. Without a registered
   * authorization request (initiated at /oauth2/authorization/{registrationId}, the corresponding
   * security filters (e.g. OAuth2AuthorizationCodeGrantFilter) will not attempt code exchange.
   */
  @Bean
  AuthorizationRequestRepository authorizationRequestRepository() {
    return new HttpSessionOAuth2AuthorizationRequestRepository(); // the default
  }

  @Bean
  OAuth2AuthorizedClientManager auth2AuthorizedClientManager() {

    // support auth code and refresh token
    OAuth2AuthorizedClientProvider auth2AuthorizedClientProvider =
        OAuth2AuthorizedClientProviderBuilder.builder()
        .authorizationCode()
        .refreshToken()
        .build();

    // Twitch requires periodic calls to verify token validity for user-token API usage
    // (https://dev.twitch.tv/docs/authentication#validating-requests), so we must use
    // the AuthorizedClientServiceOAuth2AuthorizedClientManager because it supports making
    // requests outside the HTTP context (e.g. via scheduled calls for example)
    AuthorizedClientServiceOAuth2AuthorizedClientManager clientManager =
        new AuthorizedClientServiceOAuth2AuthorizedClientManager(
          clientRegistrationRepository(),
          authorizedClientService()
        );
    clientManager.setAuthorizedClientProvider(auth2AuthorizedClientProvider);

    return clientManager;
  }

  /**
   * Stores the client settings for each client we define, for example the Twitch client id, secret,
   * auth url, etc.
   */
  @Bean
  ClientRegistrationRepository clientRegistrationRepository() {

    List<ClientRegistration> clientRegistrations = new ArrayList<>();
    clientRegistrations.add(twitchOauth2ClientRegistration());

    // TODO: Swap with our own impl that uses JDBC / JPA
    ClientRegistrationRepository clientRegistrationRepository =
        new InMemoryClientRegistrationRepository(clientRegistrations);

    return clientRegistrationRepository;
  }

  /**
   * Responsible for persisting **Authorized** Clients; e.g. Clients that are associated with an
   * Access Token + Principal In our case, we use our own implementation that persists all requests
   * under the SYSTEM user since all access is entirely local and therefore all is anonymous (no
   * Principal)
   */
  @Bean
  OAuth2AuthorizedClientRepository authorizedClientRepository() {
    return new SystemPrincipalOauth2AuthorizedClientRepository(authorizedClientService());
  }

  /**
   * Define the ClientRegistration for the Twitch Client. TODO: Move this into a custom
   * ClientRegistrationRepository so it can be rebuilt at runtime
   */
  @Bean
  ClientRegistration twitchOauth2ClientRegistration() {

    TwitchAuthConnectionSettings settings = twitchAuthConnectionSettingsFactory.getSettings();

    ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("twitch")
        .authorizationUri(settings.getHost() + settings.getAuthorizationPath())
        .tokenUri(settings.getHost() + settings.getTokenPath())
        .clientId(settings.getClientId())
        .clientSecret(settings.getClientSecret())
        .redirectUri(settings.getRedirectUri())
        .scope(settings.getScopes())
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
        .build();

    return clientRegistration;
  }

  @Bean
  TwitchAuthScheduler twitchAuthScheduler() {
    return new TwitchAuthScheduler(authorizedClientService(), auth2AuthorizedClientManager());
  }

}
