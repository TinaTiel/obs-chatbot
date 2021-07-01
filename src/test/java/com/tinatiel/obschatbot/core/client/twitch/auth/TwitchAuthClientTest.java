package com.tinatiel.obschatbot.core.client.twitch.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import com.tinatiel.obschatbot.core.user.User;
import com.tinatiel.obschatbot.security.owner.OwnerDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import java.util.UUID;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

public class TwitchAuthClientTest {

  public static MockWebServer twitchServer;

  OwnerService ownerService;
  OwnerDto owner;
  OAuth2AuthorizedClientService authorizedClientService;
  TwitchAuthConnectionSettingsFactory authSettingsFactory;
  TwitchAuthConnectionSettings authSettings;

  TwitchAuthClient twitchAuthClient;

  @BeforeAll
  static void beforeAll() throws Exception {
    twitchServer = new MockWebServer();
    twitchServer.start();
  }

  @AfterAll
  static void afterAll() throws Exception {
    twitchServer.shutdown();
  }

  @BeforeEach
  void setUp() {

    // Setup the auth client
    ownerService = mock(OwnerService.class);
    owner = (OwnerDto.builder()
      .id(UUID.randomUUID())
      .name("foo")
      .build());
    when(ownerService.getOwner()).thenReturn(owner);
    authorizedClientService = mock(OAuth2AuthorizedClientService.class);
    authSettingsFactory = mock(TwitchAuthConnectionSettingsFactory.class);
    twitchAuthClient = new TwitchAuthClientImpl(ownerService, authorizedClientService, authSettingsFactory);

    // Setup the app settings
    String twitchHost = "http://localhost:" + twitchServer.getPort();
    authSettings = TwitchAuthConnectionSettings.builder()
      .host(twitchHost)
      .clientId("clientId")
      .validationPath("/somepath/validate")
      .build();
    when(authSettingsFactory.getSettings()).thenReturn(authSettings);

  }

  @Test
  void isCurrentTokenValid_twitchFindsTokenValid() {

    // Given the app was authorized to access twitch already
    givenApplicationWasAuthorizedToAccessTwitch("validtoken", "someclientid");

    // Given twitch finds our token valid
    String json = "{\n"
      + "    \"client_id\": \"someclientid\",\n"
      + "    \"login\": \"robotiel\",\n"
      + "    \"scopes\": [\n"
      + "        \"channel:moderate\",\n"
      + "        \"chat:edit\",\n"
      + "        \"chat:read\"\n"
      + "    ],\n"
      + "    \"user_id\": \"123456\",\n"
      + "    \"expires_in\": 8609\n"
      + "}";
    twitchServer.enqueue(
      new MockResponse()
        .setResponseCode(200)
        .setHeader("Content-Type", "application/json")
        .setBody(json)
    );

    // When the token is validated
    boolean result = twitchAuthClient.isCurrentAccessTokenValid();

    // Then it is valid
    assertThat(result).isTrue();

  }

  @Test
  void isCurrentTokenValid_noTokenToValidate() {

    // Given the app was NOT authorized to access twitch
    // (do nothing)

    // When the token is validated
    boolean result = twitchAuthClient.isCurrentAccessTokenValid();

    // Then it is invalid
    assertThat(result).isFalse();

  }

  @Test
  void isCurrentTokenValid_twitchFindsTokenInvalid() {

    // Given the app was authorized to access twitch already
    givenApplicationWasAuthorizedToAccessTwitch("validtoken", "someclientid");

    // Given twitch finds our token invalid
    String json = "{\n"
      + "    \"status\": 401,\n"
      + "    \"message\": \"invalid access token\"\n"
      + "}";
    twitchServer.enqueue(
      new MockResponse()
        .setResponseCode(401)
        .setHeader("Content-Type", "application/json")
        .setBody(json)
    );

    // When the token is validated
    boolean result = twitchAuthClient.isCurrentAccessTokenValid();

    // Then it is invalid
    assertThat(result).isFalse();

  }

  @Test
  void isCurrentTokenValid_twitchError() {

    // Given the app was authorized to access twitch already
    givenApplicationWasAuthorizedToAccessTwitch("validtoken", "someclientid");

    // Given twitch responds with an error
    twitchServer.enqueue(new MockResponse().setResponseCode(400));
    twitchServer.enqueue(new MockResponse().setResponseCode(504));

    // When the token is validated, then the token is invalid
    assertThat(twitchAuthClient.isCurrentAccessTokenValid()).isFalse();
    assertThat(twitchAuthClient.isCurrentAccessTokenValid()).isFalse();

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
    when(authorizedClientService.loadAuthorizedClient("twitch", owner.getName()))
      .thenReturn(authorizedClient);
    when(authorizedClient.getAccessToken()).thenReturn(accessToken);
    when(accessToken.getTokenValue()).thenReturn(tokenValue);
    when(authorizedClient.getClientRegistration()).thenReturn(clientRegistration);
  }

}
