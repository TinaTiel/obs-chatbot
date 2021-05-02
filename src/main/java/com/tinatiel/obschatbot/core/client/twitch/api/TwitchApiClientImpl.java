package com.tinatiel.obschatbot.core.client.twitch.api;

import com.tinatiel.obschatbot.core.user.User;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
public class TwitchApiClientImpl implements TwitchApiClient {

  private WebClient webClient;
  private final OAuth2AuthorizedClientService authorizedClientService;
  private final TwitchApiClientSettingsFactory apiSettingsFactory;

  public TwitchApiClientImpl(
    OAuth2AuthorizedClientService authorizedClientService,
    TwitchApiClientSettingsFactory apiSettingsFactory) {
    this.authorizedClientService = authorizedClientService;
    this.apiSettingsFactory = apiSettingsFactory;
    init();
  }

  private void init() {
    webClient = WebClient.builder().build();
  }

  @Override
  public boolean isFollowing(String broadcasterId, String viewerId) {

    // Set the default result
    boolean result = false;

    // Get the current token and client id
    OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
      "twitch", User.SYSTEM_PRINCIPAL_NAME
    );
    if(authorizedClient == null || authorizedClient.getAccessToken() == null) {
      log.warn("No authorized client available, ignoring request");
      return result;
    }
    String accessToken = authorizedClient.getAccessToken().getTokenValue();
    String clientId = authorizedClient.getClientRegistration().getClientId();

    // Get the other settings
    TwitchApiClientSettings settings = apiSettingsFactory.getSettings();

    UsersFollowsResponse response = null;
    try {
      response = webClient
        .get()
        .uri(settings.getHost() + settings.getUsersFollowsPath()
          + "?from_id={viewer}&to_id={broadcaster}", viewerId, broadcasterId)
        .headers(headers -> {
          headers.add("Client-Id", clientId);
          headers.add("Authorization", "Bearer " + accessToken);
        })
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<UsersFollowsResponse>(){})
        .block(); // TODO make the project reactive so we don't need to block XD
    } catch (WebClientResponseException e) {
      log.warn(String.format(
        "Could not determine if viewer id %s follows broadcaster id %s, due to %s \n%s",
        viewerId, broadcasterId,
        e.getStatusCode(),
        e.getResponseBodyAsString()
        ),
        e);
    }

    if(response != null && response.getTotal() > 0) {
      result = true;
    }

    return result;

  }

  @Override
  public String getUserIdFromUsername(String username) {

    // Set the default result
    String result = null;

    // Get the current token and client id
    OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
      "twitch", User.SYSTEM_PRINCIPAL_NAME
    );
    if(authorizedClient == null || authorizedClient.getAccessToken() == null) {
      log.warn("No authorized client available, ignoring request");
      return result;
    }
    String accessToken = authorizedClient.getAccessToken().getTokenValue();
    String clientId = authorizedClient.getClientRegistration().getClientId();

    // Get the other settings
    TwitchApiClientSettings settings = apiSettingsFactory.getSettings();

    TwitchResponse<UsersDataResponse> response = null;
    try {
      response = webClient
        .get()
        .uri(settings.getHost() + settings.getUsersPath() + "?login="
          + URLEncoder.encode(username, StandardCharsets.UTF_8)
        )
        .headers(headers -> {
          headers.add("Client-Id", clientId);
          headers.add("Authorization", "Bearer " + accessToken);
        })
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<TwitchResponse<UsersDataResponse>>(){})
        .block(); // TODO make the project reactive so we don't need to block XD
    } catch (WebClientResponseException e) {
      log.warn(String.format(
        "Could not retrieve id of username %s from Twitch, due to %s \n%s",
        username,
        e.getStatusCode(),
        e.getResponseBodyAsString()
      ),
      e);
    }

    if(response != null
        && response.getData() != null
        && response.getData().size() >= 1) {
      result = response.getData().get(0).getId();
    }

    return result;
  }

}
