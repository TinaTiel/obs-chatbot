package com.tinatiel.obschatbot.core.client.twitch.auth;

import com.tinatiel.obschatbot.core.client.twitch.auth.event.TwitchAuthValidationFailureEvent;
import com.tinatiel.obschatbot.core.client.twitch.auth.event.TwitchAuthValidationSuccessEvent;
import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import com.tinatiel.obschatbot.core.messaging.QueueClient;
import com.tinatiel.obschatbot.core.user.User;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Provides means to periodically validate a Twitch access token (per their guidelines) and
 * to also refresh a token programmatically if expired.
 */
@Slf4j
public class TwitchAuthValidationService {

  private final OAuth2AuthorizedClientService authorizedClientService;
  private final OAuth2AuthorizedClientManager authorizedClientManager;
  private final RestTemplate restTemplate;
  private final TwitchAuthConnectionSettingsFactory twitchAuthConnectionSettingsFactory;
  private final QueueClient<ObsChatbotEvent> twitchAuthQueueClient;

  public TwitchAuthValidationService(
    OAuth2AuthorizedClientService authorizedClientService,
    OAuth2AuthorizedClientManager authorizedClientManager,
    RestTemplate restTemplate,
    TwitchAuthConnectionSettingsFactory twitchAuthConnectionSettingsFactory,
    QueueClient<ObsChatbotEvent> twitchAuthQueueClient) {
    this.authorizedClientService = authorizedClientService;
    this.authorizedClientManager = authorizedClientManager;
    this.restTemplate = restTemplate;
    this.twitchAuthConnectionSettingsFactory = twitchAuthConnectionSettingsFactory;
    this.twitchAuthQueueClient = twitchAuthQueueClient;
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

    // Load the most recent settings for authentication with Twitch
    TwitchAuthConnectionSettings settings = twitchAuthConnectionSettingsFactory.getSettings();

    // Get the twitch client
    OAuth2AuthorizedClient twitchClient = authorizedClientService
      .loadAuthorizedClient("twitch", User.SYSTEM_PRINCIPAL_NAME);

    // If no authorized client, submit a failure event and exit
    if(twitchClient == null || twitchClient.getAccessToken() == null) {
      twitchAuthQueueClient.submit(new TwitchAuthValidationFailureEvent("No authorized client to validate; must authorize first!"));
      return;
    }

    HttpHeaders headers = new HttpHeaders();
    headers.put("Authorization", Collections.singletonList("OAuth " + twitchClient.getAccessToken().getTokenValue()));

    HttpEntity entity = new HttpEntity(null, headers);
    ResponseEntity<Object> response;
    try{
      response = restTemplate.exchange(settings.getHost() + settings.getValidationPath(), HttpMethod.GET, entity, Object.class);
      if(response.getStatusCode() == HttpStatus.OK) {
        log.debug("Validation Success");
        twitchAuthQueueClient.submit(new TwitchAuthValidationSuccessEvent());
      }
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
        log.debug("Validation failure");
        twitchAuthQueueClient.submit(new TwitchAuthValidationFailureEvent("Access token was no-longer valid"));
      } else {
        String message = "Unexpected response from Twitch while validating token: "
          + e.getStatusCode() + ", with body: \n" + e.getResponseBodyAsString();
        twitchAuthQueueClient.submit(new TwitchAuthValidationFailureEvent(message));
        log.error(message);
      }
    }

  }

}
