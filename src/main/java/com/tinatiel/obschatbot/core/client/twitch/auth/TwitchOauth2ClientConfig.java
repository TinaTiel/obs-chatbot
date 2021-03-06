package com.tinatiel.obschatbot.core.client.twitch.auth;

import com.tinatiel.obschatbot.core.client.twitch.auth.messaging.TwitchAuthClientMessagingGateway;
import com.tinatiel.obschatbot.security.SystemPrincipalOauth2AuthorizedClientRepository;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

/**
 * Encompasses all settings required to authenticate with Twitch.
 */

@EnableScheduling
@Slf4j
@Configuration
public class TwitchOauth2ClientConfig {

  @Autowired
  TwitchAuthConnectionSettingsFactory twitchAuthConnectionSettingsFactory;

  @Autowired
  TwitchAuthClientMessagingGateway twitchAuthQueueClient;

  @Autowired
  JdbcTemplate jdbcTemplate;

  @Autowired
  OwnerService ownerService;

  /**
   * manages **authorized** clients TODO Replace with JdbcOAuth2AuthorizedClientService
   * https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/oauth2/client/JdbcOAuth2AuthorizedClientService.html
   */
  @Bean
  OAuth2AuthorizedClientService authorizedClientService() {
    return new JdbcOAuth2AuthorizedClientService(jdbcTemplate, clientRegistrationRepository());
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

    // TODO: Swap with our own impl that uses JDBC / JPA
    ClientRegistrationRepository clientRegistrationRepository =
        new JdbcClientRegistrationRepository(twitchAuthConnectionSettingsFactory);

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
    return new SystemPrincipalOauth2AuthorizedClientRepository(ownerService, authorizedClientService());
  }

  @Bean
  TwitchAuthClient twitchAuthClient() {
    return new TwitchAuthClientImpl(
      ownerService, authorizedClientService(),
      twitchAuthConnectionSettingsFactory
    );
  }

  @Bean
  TwitchAuthValidationService twitchAuthScheduler() {
    return new TwitchAuthValidationService(
      ownerService, authorizedClientService(),
      auth2AuthorizedClientManager(),
      twitchAuthQueueClient,
      twitchAuthClient()
    );
  }

}
