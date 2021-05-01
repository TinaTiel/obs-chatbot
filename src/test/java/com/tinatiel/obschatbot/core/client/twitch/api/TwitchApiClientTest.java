package com.tinatiel.obschatbot.core.client.twitch.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.core.client.twitch.auth.TwitchAuthConnectionSettings;
import com.tinatiel.obschatbot.core.client.twitch.auth.TwitchAuthConnectionSettingsFactory;
import com.tinatiel.obschatbot.core.user.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.client.RestTemplate;

public class TwitchApiClientTest {

  RestTemplate restTemplate;
  OAuth2AuthorizedClientService authorizedClientService;
  TwitchAuthConnectionSettingsFactory settingsFactory;

  TwitchApiClient twitchApiClient;

  @BeforeEach
  void setUp() {
    restTemplate = mock(RestTemplate.class);
    authorizedClientService = mock(OAuth2AuthorizedClientService.class);
    settingsFactory = mock(TwitchAuthConnectionSettingsFactory.class);
    twitchApiClient = new TwitchApiClientImpl(restTemplate, authorizedClientService, settingsFactory);
  }

  @Test
  void isCurrentTokenValid_twitchFindsTokenValid() {

    // Given the app was configured with the host and path
    String host = "https://somehost";
    String validationPath = "/some/validation";
    givenApplicationConfiguredWithHostAndValidationPath(host, validationPath);

    // Given the app was authorized to access twitch already
    String tokenValue = "validtoken";
    String clientId = "someclientid";
    givenApplicationWasAuthorizedToAccessTwitch(tokenValue, clientId);

    // Given twitch finds our token valid
    ResponseEntity response = mock(ResponseEntity.class);
    when(restTemplate.exchange(contains(validationPath), eq(HttpMethod.GET), any(), any(Class.class)))
      .thenReturn(response);
    when(response.getStatusCode()).thenReturn(HttpStatus.OK);

    // When the token is validated
    boolean result = twitchApiClient.isCurrentAccessTokenValid();

    // Then it is true
    assertThat(result).isTrue();

  }

  @Test
  void isCurrentTokenValid_noTokenToValidate() {

    // Given the app was configured with the host and path
    String host = "https://somehost";
    String validationPath = "/some/validation";
    givenApplicationConfiguredWithHostAndValidationPath(host, validationPath);

    // But Given the app was NOT authorized to access twitch
    // (do nothing)

    // When the token is validated
    boolean result = twitchApiClient.isCurrentAccessTokenValid();

    // Then it is false
    assertThat(result).isFalse();

  }

  @Test
  void isCurrentTokenValid_twitchFindsTokenInvalid() {

    // Given the app was configured with the host and path
    String host = "https://somehost";
    String validationPath = "/some/validation";
    givenApplicationConfiguredWithHostAndValidationPath(host, validationPath);

    // Given the app was authorized to access twitch already
    String tokenValue = "validtoken";
    String clientId = "someclientid";
    givenApplicationWasAuthorizedToAccessTwitch(tokenValue, clientId);

    // Given twitch finds our token NOT valid
    ResponseEntity response = mock(ResponseEntity.class);
    when(restTemplate.exchange(contains(validationPath), eq(HttpMethod.GET), any(), any(Class.class)))
      .thenReturn(response);
    when(response.getStatusCode()).thenReturn(HttpStatus.UNAUTHORIZED);

    // When the token is validated
    boolean result = twitchApiClient.isCurrentAccessTokenValid();

    // Then it is true
    assertThat(result).isFalse();
  }

  @Test
  void isFollowing_viewerIsFollowingBroadcaster() {

    // Given the app was authorized to access twitch already
    String tokenValue = "validtoken";
    String clientId = "someclientid";
    givenApplicationWasAuthorizedToAccessTwitch(tokenValue, clientId);

    // And given Twitch lists the broadcaster as a follow
    UsersFollowsResponse body = new UsersFollowsResponse(1);
    ResponseEntity<UsersFollowsResponse> responseEntity = ResponseEntity.of(Optional.of(body));
    when(restTemplate.exchange(
      contains("/users/follows?from_id={viewer}&to_id={broadcaster}"),
      eq(HttpMethod.GET),
      any(),
      eq(new ParameterizedTypeReference<UsersFollowsResponse>(){}),
      anyString(),
      anyString())
    ).thenReturn(responseEntity);

    // When called
    boolean result = twitchApiClient.isFollowing("broadcasterid", "viewerid");

    // Then the viewer is following the broadcaster
    assertThat(result).isTrue();

  }

  @Test
  void isFollowing_viewerIsNotFollowingBroadcaster() {

    // Given the app was authorized to access twitch already
    String tokenValue = "validtoken";
    String clientId = "someclientid";
    givenApplicationWasAuthorizedToAccessTwitch(tokenValue, clientId);

    // And given Twitch lists the broadcaster as a follow
    UsersFollowsResponse body = new UsersFollowsResponse(0);
    ResponseEntity<UsersFollowsResponse> responseEntity = ResponseEntity.of(Optional.of(body));
    when(restTemplate.exchange(
      contains("/users/follows?from_id={viewer}&to_id={broadcaster}"),
      eq(HttpMethod.GET),
      any(),
      eq(new ParameterizedTypeReference<UsersFollowsResponse>(){}),
      anyString(),
      anyString())
    ).thenReturn(responseEntity);

    // When called
    boolean result = twitchApiClient.isFollowing("broadcasterid", "viewerid");

    // Then the viewer is following the broadcaster
    assertThat(result).isFalse();

  }

  @Test
  void isFollowing_notAuthorized() {

    // Given the app was not authorized to access twitch already
    // (do nothing)

    // When called
    boolean result = twitchApiClient.isFollowing("broadcasterid", "viewerid");

    // Then the viewer is not following the broadcaster (we don't throw an exception)
    assertThat(result).isFalse();

  }

  private void givenApplicationWasAuthorizedToAccessTwitch(String clientId, String tokenValue) {
    OAuth2AuthorizedClient authorizedClient = mock(OAuth2AuthorizedClient.class);
    OAuth2AccessToken accessToken = mock(OAuth2AccessToken.class);
    ClientRegistration clientRegistration = ClientRegistration
      .withRegistrationId("twitch")
      .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE) // doesn't matter for our test, but required by Spring
      .redirectUri("doesntmatter")
      .authorizationUri("doesntmatter")
      .tokenUri("doesntmatter")
      .clientId(clientId)
      .build();
    when(authorizedClientService.loadAuthorizedClient("twitch", User.SYSTEM_PRINCIPAL_NAME))
      .thenReturn(authorizedClient);
    when(authorizedClient.getAccessToken()).thenReturn(accessToken);
    when(accessToken.getTokenValue()).thenReturn(tokenValue);
    when(authorizedClient.getClientRegistration()).thenReturn(clientRegistration);
  }

  private void givenApplicationConfiguredWithHostAndValidationPath(String host, String validationPath) {
    TwitchAuthConnectionSettings settings = mock(TwitchAuthConnectionSettings.class);
    when(settingsFactory.getSettings()).thenReturn(settings);
    when(settings.getHost()).thenReturn(host);
    when(settings.getValidationPath()).thenReturn(validationPath);
  }

}
