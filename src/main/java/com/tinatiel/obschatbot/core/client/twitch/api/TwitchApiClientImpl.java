package com.tinatiel.obschatbot.core.client.twitch.api;

import com.tinatiel.obschatbot.core.user.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.client.RestTemplate;

public class TwitchApiClientImpl implements TwitchApiClient {

  RestTemplate restTemplate;
  private final OAuth2AuthorizedClientService authorizedClientService;

  public TwitchApiClientImpl(
    OAuth2AuthorizedClientService authorizedClientService) {
    this.authorizedClientService = authorizedClientService;
    this.restTemplate = new RestTemplate();
    this.restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
  }

  @Override
  public boolean isFollowing(String broadcasterId, String viewerId) {
    // Get the authorized client
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

}
