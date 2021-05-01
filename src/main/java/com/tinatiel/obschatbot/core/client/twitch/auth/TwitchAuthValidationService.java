package com.tinatiel.obschatbot.core.client.twitch.auth;

import com.tinatiel.obschatbot.core.client.twitch.api.TwitchApiClient;
import com.tinatiel.obschatbot.core.client.twitch.auth.event.TwitchAuthValidationFailureEvent;
import com.tinatiel.obschatbot.core.client.twitch.auth.event.TwitchAuthValidationSuccessEvent;
import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import com.tinatiel.obschatbot.core.messaging.QueueClient;
import com.tinatiel.obschatbot.core.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

/**
 * Provides means to periodically validate a Twitch access token (per their guidelines) and
 * to also refresh a token programmatically if expired.
 */
@Slf4j
public class TwitchAuthValidationService {

  private final OAuth2AuthorizedClientService authorizedClientService;
  private final OAuth2AuthorizedClientManager authorizedClientManager;
  private final QueueClient<ObsChatbotEvent> twitchAuthQueueClient;
  private final TwitchApiClient twitchApiClient;

  public TwitchAuthValidationService(
    OAuth2AuthorizedClientService authorizedClientService,
    OAuth2AuthorizedClientManager authorizedClientManager,
    QueueClient<ObsChatbotEvent> twitchAuthQueueClient,
    TwitchApiClient twitchApiClient) {
    this.authorizedClientService = authorizedClientService;
    this.authorizedClientManager = authorizedClientManager;
    this.twitchAuthQueueClient = twitchAuthQueueClient;
    this.twitchApiClient = twitchApiClient;
  }

  /**
   * Periodically check our token doesn't need to be refreshed. We cannot
   * wait for this to be handled normally by the web filters because the token
   * is used in our IRC client -- so, if the token needs to be refreshed, then we
   * must also stop/start the IRC client as well.
   */
  @Scheduled(fixedRate = 1000*60*5) // every 5 minutes
  public void refreshTokenIfNeeded() {

    // Get the twitch client
    OAuth2AuthorizedClient twitchClient = authorizedClientService
      .loadAuthorizedClient("twitch", User.SYSTEM_PRINCIPAL_NAME);

    // If there was no authorized client, then log a warning TODO communicate/keep state to show user
    if(twitchClient == null) {
      // TODO do something to show to user
      log.warn("Twitch client hasn't been authorized");
      return;
    }

    // If the token will expire in the next 10 minutes, then refresh it
    authorizedClientManager.authorize(
      OAuth2AuthorizeRequest
        .withAuthorizedClient(twitchClient)
        .principal(User.SYSTEM_PRINCIPAL_NAME)
        .build()
    );

  }

  /**
   * Validates the current Twitch access token is still valid, as required per their guidelines.
   * Since our usage is tied to chat, we schedule periodic checks rather than every call.
   */
  @Scheduled(fixedRate = 1000*60*30) // every 30 minutes -- Twitch requires at least once per hour
  public void validateToken() {

    if(twitchApiClient.isCurrentAccessTokenValid()) {
      twitchAuthQueueClient.submit(new TwitchAuthValidationSuccessEvent());
    } else {
      twitchAuthQueueClient.submit(new TwitchAuthValidationFailureEvent());
    }

  }

}
