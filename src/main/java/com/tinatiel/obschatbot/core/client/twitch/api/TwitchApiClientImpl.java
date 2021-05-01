package com.tinatiel.obschatbot.core.client.twitch.api;

import com.tinatiel.obschatbot.core.client.twitch.auth.TwitchAuthConnectionSettings;
import com.tinatiel.obschatbot.core.client.twitch.auth.TwitchAuthConnectionSettingsFactory;
import com.tinatiel.obschatbot.core.client.twitch.auth.event.TwitchAuthValidationFailureEvent;
import com.tinatiel.obschatbot.core.client.twitch.auth.event.TwitchAuthValidationSuccessEvent;
import com.tinatiel.obschatbot.core.user.User;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class TwitchApiClientImpl implements TwitchApiClient {

  private final RestTemplate restTemplate;
  private final OAuth2AuthorizedClientService authorizedClientService;
  private final TwitchAuthConnectionSettingsFactory settingsFactory;

  public TwitchApiClientImpl(RestTemplate restTemplate,
    OAuth2AuthorizedClientService authorizedClientService,
    TwitchAuthConnectionSettingsFactory settingsFactory) {
    this.restTemplate = restTemplate;
    this.authorizedClientService = authorizedClientService;
    this.settingsFactory = settingsFactory;
  }

  @Override
  public boolean isFollowing(String broadcasterId, String viewerId) {

    // Get the authorized client; if none, then exit early assuming the viewer doesn't follow the broadcaster
    OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient("twitch", User.SYSTEM_PRINCIPAL_NAME);
    if(client == null || client.getAccessToken() == null) return false;

    // Add authentication headers
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + client.getAccessToken().getTokenValue());
    headers.add("Client-Id", client.getClientRegistration().getClientId());

    HttpEntity entity = new HttpEntity(headers);
    // Make the request
    UsersFollowsResponse response = restTemplate.exchange(
      "https://api.twitch.tv/helix/users/follows?from_id={viewer}&to_id={broadcaster}",
      HttpMethod.GET,
      entity,
      new ParameterizedTypeReference<UsersFollowsResponse>(){},
      viewerId, broadcasterId).getBody();

    return response != null && response.getTotal() > 0;

  }

  @Override
  public boolean isCurrentAccessTokenValid() {

    // We assume the token isn't valid unless Twitch proves otherwise
    boolean tokenIsValid = false;

    // Load the most recent settings for authentication with Twitch
    TwitchAuthConnectionSettings settings = settingsFactory.getSettings();

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
