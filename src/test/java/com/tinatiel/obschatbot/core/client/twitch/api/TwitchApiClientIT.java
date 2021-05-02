package com.tinatiel.obschatbot.core.client.twitch.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.core.user.User;
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
import org.springframework.web.client.RestTemplate;

public class TwitchApiClientIT {

  public static MockWebServer twitchServer;
  TwitchApiClientSettings apiSettings;

  OAuth2AuthorizedClientService authorizedClientService;
  TwitchApiClientSettingsFactory apiSettingsFactory;

  TwitchApiClient twitchApiClient;

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

    // Setup the app settings
    String twitchHost = "http://localhost:" + twitchServer.getPort();
    apiSettings = TwitchApiClientSettings.builder()
      .host(twitchHost)
      .usersFollowsPath("/helix/users/follows")
      .usersPath("/helix/users")
      .build();


    // Setup the SUT
    authorizedClientService = mock(OAuth2AuthorizedClientService.class);
    apiSettingsFactory = mock(TwitchApiClientSettingsFactory.class);
    when(apiSettingsFactory.getSettings()).thenReturn(apiSettings);

    twitchApiClient = new TwitchApiClientImpl(authorizedClientService, apiSettingsFactory);

  }

  @Test
  void isFollowing_viewerIsFollowingBroadcaster() {

    // Given the app was authorized to access twitch already
    givenApplicationWasAuthorizedToAccessTwitch("someclient", "sometoken");

    // And given Twitch lists the broadcaster as a follow
    String json = "{\n"
      + "    \"total\": 1,\n"
      + "    \"data\": [\n"
      + "        {\n"
      + "            \"from_id\": \"654321\",\n"
      + "            \"from_login\": \"robotiel\",\n"
      + "            \"from_name\": \"robotiel\",\n"
      + "            \"to_id\": \"123456\",\n"
      + "            \"to_login\": \"tinatiel\",\n"
      + "            \"to_name\": \"tinatiel\",\n"
      + "            \"followed_at\": \"2021-05-02T11:20:19Z\"\n"
      + "        }\n"
      + "    ],\n"
      + "    \"pagination\": {}\n"
      + "}";

    twitchServer.enqueue(new MockResponse()
      .setResponseCode(200)
      .setBody(json)
      .setHeader("Content-Type", "application/json")
    );

    // When called
    boolean result = twitchApiClient.isFollowing("123456", "654321");

    // Then the viewer is following the broadcaster
    assertThat(result).isTrue();

  }

  @Test
  void isFollowing_viewerIsNotFollowingBroadcaster() {

    // Given the app was authorized to access twitch already
    givenApplicationWasAuthorizedToAccessTwitch("someclient", "sometoken");

    // And given Twitch lists the broadcaster as a follow
    String json = "{\n"
      + "    \"total\": 0,\n"
      + "    \"data\": [],\n"
      + "    \"pagination\": {}\n"
      + "}";

    twitchServer.enqueue(new MockResponse()
      .setResponseCode(200)
      .setBody(json)
      .setHeader("Content-Type", "application/json")
    );

    // When called
    boolean result = twitchApiClient.isFollowing("123456", "654321");

    // Then the viewer is NOT following the broadcaster
    assertThat(result).isFalse();

  }

  @Test
  void isFollowing_twitchError() {

    // Given the app was authorized to access twitch already
    givenApplicationWasAuthorizedToAccessTwitch("someclient", "sometoken");

    // And given Twitch responds with errors
    twitchServer.enqueue(new MockResponse().setResponseCode(400));
    twitchServer.enqueue(new MockResponse().setResponseCode(504));

    // When called, then the viewer is not following the broadcaster
    assertThat(twitchApiClient.isFollowing("123456", "654321")).isFalse();
    assertThat(twitchApiClient.isFollowing("123456", "654321")).isFalse();

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

  @Test
  void getUserId_userFound() {

    // Given the api client was authorized
    givenApplicationWasAuthorizedToAccessTwitch("someclient", "sometoken");

    // Given server responds with a payload
    String json = "{\n"
      + "  \"data\": [\n"
      + "    {\n"
      + "      \"id\": \"141981764\",\n"
      + "      \"login\": \"twitchdev\",\n"
      + "      \"display_name\": \"TwitchDev\",\n"
      + "      \"type\": \"\",\n"
      + "      \"broadcaster_type\": \"partner\",\n"
      + "      \"description\": \"Supporting third-party developers building Twitch integrations from chatbots to game integrations.\",\n"
      + "      \"profile_image_url\": \"https://static-cdn.jtvnw.net/jtv_user_pictures/8a6381c7-d0c0-4576-b179-38bd5ce1d6af-profile_image-300x300.png\",\n"
      + "      \"offline_image_url\": \"https://static-cdn.jtvnw.net/jtv_user_pictures/3f13ab61-ec78-4fe6-8481-8682cb3b0ac2-channel_offline_image-1920x1080.png\",\n"
      + "      \"view_count\": 5980557,\n"
      + "      \"email\": \"not-real@email.com\",\n"
      + "      \"created_at\": \"2016-12-14T20:32:28.894263Z\"\n"
      + "    }\n"
      + "  ]\n"
      + "}";
    twitchServer.enqueue(new MockResponse()
      .setResponseCode(200)
      .setBody(json)
      .addHeader("Content-Type", "application/json")
    );

    // When the client requests the id
    String id = twitchApiClient.getUserIdFromUsername("twitchdev");

    // Then it matches what we expect
    assertThat(id).isEqualTo("141981764");

  }

  @Test
  void getUserId_userNotFound() {

    // Given the api client was authorized
    givenApplicationWasAuthorizedToAccessTwitch("someclient", "sometoken");

    // Given server responds with no payload
    String json = "{\n"
      + "  \"data\": [\n"
      + "  ]\n"
      + "}";
    twitchServer.enqueue(new MockResponse()
      .setResponseCode(200)
      .setBody(json)
      .addHeader("Content-Type", "application/json")
    );

    // When the client requests the id
    String id = twitchApiClient.getUserIdFromUsername("twitchdev");

    // Then it is null
    assertThat(id).isNull();

  }

  @Test
  void getUserId_twitchErrorResponse() {

    // Given the api client was authorized
    givenApplicationWasAuthorizedToAccessTwitch("someclient", "sometoken");

    // Given server responds with an error
    twitchServer.enqueue(new MockResponse().setResponseCode(400));
    twitchServer.enqueue(new MockResponse().setResponseCode(504));

    // When the client requests the id, then it is null
    assertThat(twitchApiClient.getUserIdFromUsername("twitchdev")).isNull();
    assertThat(twitchApiClient.getUserIdFromUsername("twitchdev")).isNull();

  }

  @Test
  void getUserId_notAuthorized() {

    // Given the api client was NOT authorized
    // (do nothing)

    // When the client requests the id
    String id = twitchApiClient.getUserIdFromUsername("twitchdev");

    // Then it is null
    assertThat(id).isNull();

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

}
