package com.tinatiel.obschatbot.core.client.twitch.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.core.client.twitch.auth.event.TwitchAuthValidationFailureEvent;
import com.tinatiel.obschatbot.core.client.twitch.auth.event.TwitchAuthValidationSuccessEvent;
import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import com.tinatiel.obschatbot.core.messaging.QueueNotifier;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

public class TwitchAuthValidationIT extends AbstractTwitchAuthTest {

  @MockBean
  TwitchAuthConnectionSettingsFactory mockTwitchAuthConnSettingsFactory;

  @Qualifier("twitchAuthQueueNotifier")
  @MockBean
  QueueNotifier<ObsChatbotEvent> twitchAuthQueueNotifier; // mock the notifier so it doesn't consume queue events

  @MockBean
  OAuth2AuthorizedClientService authorizedClientService;

  @Autowired
  TwitchAuthValidationService twitchAuthValidationService;

  @Autowired
  BlockingQueue<ObsChatbotEvent> twitchAuthQueue;

  // Given we define min info to auth with Twitch
  String redirectUri = "http://localhost:" + localPort + "/foo"; // doesn't matter because Spring Security uses filters to detect
  String twitchBaseUrl = "http://localhost:" + twitchPort;
  String twitchAuthPath = "/somepath/authorize";
  String twitchTokenPath = "/somepath/token";
  List<String> scopes = Arrays.asList("foo:bar", "chat:read", "channel:moderate");
  String clientId = "myclientid";
  String clientSecret = "myclientsecret";
  String code = "abcd1234";
  String state = "asdfasdf";
  String accessToken = "myaccesstoken";
  String refreshToken = "myrefreshtoken";

  @BeforeEach
  void setUp() {
    twitchAuthQueue.clear();
    assertThat(twitchAuthQueue).isEmpty();
  }

  @Test
  void validateToken_Success() throws Exception {

    // Given client registered with valid settings
    String twitchValidationPath = "/oauth2/validate";
    givenClientRegisteredWithSettings(mockTwitchAuthConnSettingsFactory, TwitchAuthConnectionSettings.builder()
      .host(twitchBaseUrl)
      .authorizationPath(twitchAuthPath)
      .tokenPath(twitchTokenPath)
      .validationPath(twitchValidationPath)
      .scopes(scopes)
      .clientId(clientId)
      .clientSecret(clientSecret)
      .redirectUri(redirectUri)
      .build()
    );

    // Given we have an authorized client that returns an access token
    String accessToken = "myAccessToken";
    OAuth2AuthorizedClient authorizedClient = mock(OAuth2AuthorizedClient.class);
    OAuth2AccessToken oAuth2AccessToken = mock(OAuth2AccessToken.class);
    when(oAuth2AccessToken.getTokenValue()).thenReturn(accessToken);
    when(authorizedClient.getAccessToken()).thenReturn(oAuth2AccessToken);
    when(authorizedClientService.loadAuthorizedClient(eq("twitch"), any())).thenReturn(authorizedClient);


    // Given Twitch finds our access token is valid
    new MockServerClient("localhost", twitchPort)
      .when(
        HttpRequest.request()
          .withMethod("GET")
          .withPath(twitchValidationPath)
          .withHeader("Authorization", "OAuth " + accessToken)
      )
      .respond(
        HttpResponse.response()
          .withStatusCode(200) // yes there is a body, but we don't care about it for this, as the other status is 401
      );

    // When we validate our access token
    twitchAuthValidationService.validateToken();

    // Then there will be a valid auth event in the queue
    assertThat(twitchAuthQueue.take()).isNotNull().isInstanceOf(TwitchAuthValidationSuccessEvent.class);

  }

  @Test
  void validateToken_Failure() throws Exception {

    // Given client registered with valid settings
    String twitchValidationPath = "/oauth2/validate";
    givenClientRegisteredWithSettings(mockTwitchAuthConnSettingsFactory, TwitchAuthConnectionSettings.builder()
      .host(twitchBaseUrl)
      .authorizationPath(twitchAuthPath)
      .tokenPath(twitchTokenPath)
      .validationPath(twitchValidationPath)
      .scopes(scopes)
      .clientId(clientId)
      .clientSecret(clientSecret)
      .redirectUri(redirectUri)
      .build()
    );

    // Given we have an authorized client that returns an access token
    String accessToken = "myInvalidAccessToken";
    OAuth2AuthorizedClient authorizedClient = mock(OAuth2AuthorizedClient.class);
    OAuth2AccessToken oAuth2AccessToken = mock(OAuth2AccessToken.class);
    when(oAuth2AccessToken.getTokenValue()).thenReturn(accessToken);
    when(authorizedClient.getAccessToken()).thenReturn(oAuth2AccessToken);
    when(authorizedClientService.loadAuthorizedClient(eq("twitch"), any())).thenReturn(authorizedClient);

    // Given Twitch finds our access token is NOT valid
    new MockServerClient("localhost", twitchPort)
      .when(
        HttpRequest.request()
          .withMethod("GET")
          .withPath(twitchValidationPath)
          .withHeader("Authorization", "OAuth " + accessToken)
      )
      .respond(
        HttpResponse.response()
          .withStatusCode(401)
      );

    // When we validate our access token
    twitchAuthValidationService.validateToken();

    // Then there will be an invalid auth event in the queue
    assertThat(twitchAuthQueue.take()).isNotNull().isInstanceOf(TwitchAuthValidationFailureEvent.class);

  }

  @Test
  void validateToken_NoAuthorizedClient() throws Exception {

    // Given client registered with valid settings
    String twitchValidationPath = "/oauth2/validate";
    givenClientRegisteredWithSettings(mockTwitchAuthConnSettingsFactory, TwitchAuthConnectionSettings.builder()
      .host(twitchBaseUrl)
      .authorizationPath(twitchAuthPath)
      .tokenPath(twitchTokenPath)
      .validationPath(twitchValidationPath)
      .scopes(scopes)
      .clientId(clientId)
      .clientSecret(clientSecret)
      .redirectUri(redirectUri)
      .build()
    );

    // Given we have no authorized client yet
    when(authorizedClientService.loadAuthorizedClient(eq("twitch"), any())).thenReturn(null);

    // When we try to validate our access token
    twitchAuthValidationService.validateToken();

    // Then there will be an invalid auth event in the queue
    assertThat(twitchAuthQueue.take()).isNotNull().isInstanceOf(TwitchAuthValidationFailureEvent.class);

  }

}
