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

@Slf4j
public class TwitchAuthClientImpl implements TwitchAuthClient {

  private final RestTemplate restTemplate;
  private final OAuth2AuthorizedClientService authorizedClientService;
  private final TwitchAuthConnectionSettingsFactory authSettingsFactory;

  public TwitchAuthClientImpl(RestTemplate restTemplate,
    OAuth2AuthorizedClientService authorizedClientService,
    TwitchAuthConnectionSettingsFactory authSettingsFactory) {
    this.restTemplate = restTemplate;
    this.authorizedClientService = authorizedClientService;
    this.authSettingsFactory = authSettingsFactory;
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

    // If no authorized client, return early
    if(twitchClient == null || twitchClient.getAccessToken() == null) {
      log.warn("No authorized client to validate; must authorize first!");
      return tokenIsValid;
    }

    // Build the request auth headers
    HttpHeaders headers = new HttpHeaders();
    headers.put("Authorization", Collections.singletonList("OAuth " + twitchClient.getAccessToken().getTokenValue()));
    HttpEntity entity = new HttpEntity(null, headers);

    // Make the call
    ResponseEntity<Object> response;
    try{
      response = restTemplate.exchange(
        settings.getHost() + settings.getValidationPath(),
        HttpMethod.GET,
        entity,
        Object.class
      );

      // If the response is OK, then the token is valid
      if(response.getStatusCode() == HttpStatus.OK) {
        log.debug("Current access token is valid");
        tokenIsValid = true;
      }
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
        log.debug("Current access token is NOT valid");
      } else {
        log.error("Unexpected response from Twitch while validating token: "
          + e.getStatusCode() + ", with body: \n" + e.getResponseBodyAsString());
      }
    }
    return tokenIsValid;
  }

}
