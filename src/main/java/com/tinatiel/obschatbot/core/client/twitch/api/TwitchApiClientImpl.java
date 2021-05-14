package com.tinatiel.obschatbot.core.client.twitch.api;

import com.tinatiel.obschatbot.core.cache.CacheConfig;
import com.tinatiel.obschatbot.core.client.twitch.api.model.TwitchResponse;
import com.tinatiel.obschatbot.core.client.twitch.api.model.UsersDataResponse;
import com.tinatiel.obschatbot.core.client.twitch.api.model.UsersFollowsResponse;
import com.tinatiel.obschatbot.core.user.User;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Default implementation of the TwitchApiClient.
 */
@Slf4j
public class TwitchApiClientImpl implements TwitchApiClient {

  private final OAuth2AuthorizedClientService authorizedClientService;
  private final TwitchApiClientSettingsFactory apiSettingsFactory;
  private WebClient webClient;

  /**
   * Creates a new instance of the TwitchApiClient, internally intializing its WebClient.
   */
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

  // Commenting out until we add Twitch pub/sub/event support to invalidate cache on follow events
  // @Cacheable(CacheConfig.TWITCH_FOLLOWS)
  @Override
  public boolean isFollowing(String broadcasterId, String viewerId) {

    // Set the default result
    boolean result = false;

    // Get the current token and client id
    OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
        "twitch", User.SYSTEM_PRINCIPAL_NAME
    );
    if (authorizedClient == null || authorizedClient.getAccessToken() == null) {
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
        .bodyToMono(new ParameterizedTypeReference<UsersFollowsResponse>() {
        })
        .block(); // TODO make the project reactive so we don't need to block XD
    } catch (WebClientResponseException e) {
      log.warn(String.format(
          "Could not determine if viewer id %s follows broadcaster id %s, due to %s \n%s",
          viewerId, broadcasterId,
          e.getStatusCode(),
          e.getResponseBodyAsString()),
          e);
    }

    if (response != null && response.getTotal() > 0) {
      result = true;
    }

    return result;

  }

  @Cacheable(CacheConfig.TWITCH_USER_IDS)
  @Override
  public String getUserIdFromUsername(String username) {

    // Set the default result
    String result = null;

    // Get the current token and client id
    OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
        "twitch", User.SYSTEM_PRINCIPAL_NAME
    );
    if (authorizedClient == null || authorizedClient.getAccessToken() == null) {
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
        .bodyToMono(new ParameterizedTypeReference<TwitchResponse<UsersDataResponse>>() {
        })
        .block(); // TODO make the project reactive so we don't need to block XD
    } catch (WebClientResponseException e) {
      log.warn(String.format(
          "Could not retrieve id of username %s from Twitch, due to %s \n%s",
          username,
          e.getStatusCode(),
          e.getResponseBodyAsString()),
          e);
    }

    if (response != null
        && response.getData() != null
        && response.getData().size() >= 1) {
      result = response.getData().get(0).getId();
    }

    return result;
  }

}
