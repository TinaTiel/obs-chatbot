package com.tinatiel.obschatbot.core.client.twitch.auth;

import com.tinatiel.obschatbot.core.client.twitch.api.TwitchApiClientSettingsFactory;
import com.tinatiel.obschatbot.core.user.User;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
public class TwitchAuthClientImpl implements TwitchAuthClient {

  private WebClient webClient;
  private final OAuth2AuthorizedClientService authorizedClientService;
  private final TwitchAuthConnectionSettingsFactory authSettingsFactory;

  public TwitchAuthClientImpl(
    OAuth2AuthorizedClientService authorizedClientService,
    TwitchAuthConnectionSettingsFactory authSettingsFactory) {
    this.authorizedClientService = authorizedClientService;
    this.authSettingsFactory = authSettingsFactory;
    init();
  }

  private void init() {
    webClient = WebClient.builder().build();
  }

  @Override
  public boolean isCurrentAccessTokenValid() {

    // We assume the token isn't valid unless Twitch proves otherwise
    boolean tokenIsValid = false;

    // Load the most recent settings for authentication with Twitch
    TwitchAuthConnectionSettings settings = authSettingsFactory.getSettings();

    // Get the twitch client
    OAuth2AuthorizedClient twitchClient = authorizedClientService
      .loadAuthorizedClient("twitch", User.SYSTEM_PRINCIPAL_NAME);
    if(twitchClient == null || twitchClient.getAccessToken() == null) {
      log.warn("No authorized client to validate; must authorize first!");
      return tokenIsValid;
    }

    // Make the request
    ResponseEntity<Void> response = null;
    try {
      response = webClient
        .get()
        .uri(settings.getHost() + settings.getValidationPath())
        .header("Authorization", "OAuth " + twitchClient.getAccessToken().getTokenValue())
        .retrieve()
        .toBodilessEntity()
        .block(); // TODO don't do this
    } catch (WebClientResponseException e) {
      if(e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
        log.debug("Token is not valid");
      } else {
        log.error(
          String.format("Unable to determine token validity, due to %s\n%s",
            e.getStatusCode(), e.getResponseBodyAsString()),
          e
        );
      }
    }

    if(response != null && response.getStatusCode().is2xxSuccessful()) {
      tokenIsValid = true;
    }

    return tokenIsValid;

  }

}
