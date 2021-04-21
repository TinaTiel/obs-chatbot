package com.tinatiel.obschatbot.core.client.twitch.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockserver.model.JsonBody.json;
import static org.mockserver.model.Parameter.param;
import static org.mockserver.model.ParameterBody.params;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@TestInstance(Lifecycle.PER_CLASS) // so that we can have non-static BeforeAll initialization
@DirtiesContext // Because we have to use the defined port, aaaaaa
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT, properties = {"server.port=8080"})
public class AbstractTwitchAuthTest {

  //  @LocalServerPort // Not actually working, so we have to hard-code it and dirty the context.
  int localPort=8080;
  int twitchPort = 8090;

  private ClientAndServer mockServer;

  @BeforeAll
  public void beforeClass() {
    mockServer = ClientAndServer.startClientAndServer(twitchPort);
  }

  @AfterAll
  public void afterClass() {
    mockServer.stop();
  }

  void givenClientRegisteredWithSettings(
      TwitchAuthConnectionSettingsFactory mockFactory,
      TwitchAuthConnectionSettings settings
  ) {
    when(mockFactory.getSettings()).thenReturn(settings);
  }

  void givenTwitchRespondsWithToken(
    int twitchPort,
    String twitchTokenPath,
    String accessToken,
    int expiresIn,
    String refreshToken,
    List<String> scopes
  ) {
    givenTwitchRespondsWithTokenOfGrantType(
      twitchPort,
      twitchTokenPath,
      accessToken,
      expiresIn,
      refreshToken,
      scopes,
      "authorization_code"
    );
  }

  void givenTwitchRefreshesToken(
    int twitchPort,
    String twitchTokenPath,
    String accessToken,
    int expiresIn,
    String refreshToken,
    List<String> scopes
  ) {
    givenTwitchRespondsWithTokenOfGrantType(
      twitchPort,
      twitchTokenPath,
      accessToken,
      expiresIn,
      refreshToken,
      scopes,
      "refresh_token"
    );
  }

  void givenTwitchRespondsWithTokenOfGrantType(
    int twitchPort,
    String twitchTokenPath,
    String accessToken,
    int expiresIn,
    String refreshToken,
    List<String> scopes,
    String grantType
  ) {
    new MockServerClient("localhost", twitchPort)
      .when(
        HttpRequest.request()
          .withMethod("POST")
          .withPath(twitchTokenPath)
          .withBody(params(
            param("grant_type", grantType)
          ))
      )
      .respond(
        HttpResponse.response()
          .withStatusCode(200)
          .withHeader("Content-Type", "application/json")
          .withBody(json(createTokenJson(accessToken, expiresIn, refreshToken, scopes)))
      );
  }

  void givenTwitchRefreshesToken(
      String twitchTokenPath,
      String clientId,
      String clientSecret,
      String refreshToken,
      String newAccessToken,
      int newExpiresIn,
      String newRefreshToken,
      List<String> newScopes
  ) {
    new MockServerClient("localhost", twitchPort)
      .when(
        HttpRequest.request()
          .withMethod("POST")
          .withPath(twitchTokenPath)
      )
      .respond(
        HttpResponse.response()
          .withStatusCode(200)
          .withHeader("Content-Type", "application/json")
          .withBody(json(createTokenJson(newAccessToken, newExpiresIn, newRefreshToken, newScopes)))
      );
  }

  String createTokenJson(String accessToken, int expiresIn, String refreshToken, List<String> scopes) {
    return "{" + System.lineSeparator()
      + "\"access_token\": \"" + accessToken + "\"," + System.lineSeparator()
      + "\"expires_in\": " + expiresIn + "," + System.lineSeparator()
      + "\"refresh_token\": \"" + refreshToken + "\"," + System.lineSeparator()
      + "\"scope\": ["
        + scopes.stream().map(it -> "\"" + it + "\"").collect(Collectors.joining(","))
      + "]," + System.lineSeparator()
      + "\"token_type\": \"" + "bearer" + "\"" + System.lineSeparator()
      + "}";

//    {
//      "access_token": "1sa6c0v44g987ashdftmx7dmibzth55c6",
//      "expires_in": 13670,
//      "refresh_token": "vh8nsjnru74cqasldkjflajs8u1wgyk63c2frw3hxwcy7s3",
//      "scope": [
//      "channel:moderate",
//        "chat:edit",
//        "chat:read"
//    ],
//      "token_type": "bearer"
//    }
  }

  void givenAnAuthorizationRequestExists(
    AuthorizationRequestRepository<OAuth2AuthorizationRequest> mockAuthorizationRequestRepository,
    String twitchBaseUrl,
    String twitchAuthPath,
    List<String> scopes,
    String clientId,
    String redirectUri,
    String state
  ) {

    // Given we have an authorization request
    HashMap<String, Object> attributes = new HashMap<>();
    attributes.put("registration_id", "twitch");
    OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
      .authorizationUri(twitchBaseUrl + twitchAuthPath)
      .clientId(clientId)
      .redirectUri(redirectUri)
      .scopes(new HashSet<>(scopes))
      .state(state)
      .attributes(attributes)
      .build();
    when(mockAuthorizationRequestRepository.loadAuthorizationRequest(any()))
      .thenReturn(authorizationRequest);
    when(mockAuthorizationRequestRepository.removeAuthorizationRequest(any(), any()))
      .thenReturn(authorizationRequest);

  }

  WebTestClient buildLoggingWebClient() {
    ExchangeStrategies exchangeStrategies = ExchangeStrategies.withDefaults();
    exchangeStrategies
      .messageWriters().stream()
      .filter(LoggingCodecSupport.class::isInstance)
      .forEach(writer -> ((LoggingCodecSupport)writer).setEnableLoggingRequestDetails(true));

    return WebTestClient
      .bindToServer()
      .exchangeStrategies(exchangeStrategies)
      .build();
  }

}
