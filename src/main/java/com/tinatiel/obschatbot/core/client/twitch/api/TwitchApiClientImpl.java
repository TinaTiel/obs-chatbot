package com.tinatiel.obschatbot.core.client.twitch.api;

import com.tinatiel.obschatbot.core.user.User;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
public class TwitchApiClientImpl implements TwitchApiClient {

  private WebClient webClient;
  private RestTemplate restTemplate;
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
    this.restTemplate = new RestTemplate();
    restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    webClient = WebClient.builder().build();
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
    UsersFollowsResponse response = null;
    TwitchApiClientSettings settings = apiSettingsFactory.getSettings();
    try {
      response = restTemplate.exchange(
        settings.getHost() + settings.getUsersFollowsPath() + "?from_id={viewer}&to_id={broadcaster}",
        HttpMethod.GET,
        entity,
        new ParameterizedTypeReference<UsersFollowsResponse>(){},
        viewerId, broadcasterId).getBody();
    } catch (HttpClientErrorException e) {
      log.error("Could not determine if user is follower; twitch responded with " + e.getStatusCode());
    }

    return response != null && response.getTotal() > 0;

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

    TwitchResponse<UsersDataResponse> response = webClient
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

    if(response != null
        && response.getData() != null
        && response.getData().size() >= 1) {
      result = response.getData().get(0).getId();
    }

    return result;
  }

}
