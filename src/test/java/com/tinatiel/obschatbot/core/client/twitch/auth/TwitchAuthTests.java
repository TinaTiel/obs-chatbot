package com.tinatiel.obschatbot.core.client.twitch.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockserver.model.JsonBody.json;

import com.tinatiel.obschatbot.core.user.User;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

@TestInstance(Lifecycle.PER_CLASS) // so that we can have non-static BeforeAll initialization
@DirtiesContext // Because we have to use the defined port, aaaaaa
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT, properties = {"server.port=8080"})
public class TwitchAuthTests {

  //  @LocalServerPort // Not actually working, so we have to hard-code it and dirty the context.
  int localPort=8080;

  // Define our info to initialize the MockServer expectations
  private String redirectUri = "http://localhost:" + localPort + "/foo"; // doesn't matter because Spring Security uses filters to detect
  private int twitchPort = 8090;
  private String twitchBaseUrl = "http://localhost:" + twitchPort;
  private String twitchAuthPath = "/somepath/authorize";
  private String twitchTokenPath = "/somepath/token";
  private List<String> scopes = Arrays.asList("foo:bar");
  private String clientId = "myclientid";
  private String clientSecret = "myclientsecret";
  private String code = "abcd1234";
  private String state = "asdfasdf";
  private String accessToken = "myaccesstoken";
  private String refreshToken = "myrefreshtoken";
  private int expiresIn = 123456789;

  private ClientAndServer mockServer;

  private WebTestClient webClient;

  @MockBean
  TwitchAuthConnectionSettingsFactory twitchAuthConnectionSettingsFactory;

  @MockBean
  AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;

  @Autowired
  OAuth2AuthorizedClientService authorizedClientService;

  @BeforeAll
  public void beforeClass() {
    mockServer = ClientAndServer.startClientAndServer(twitchPort);
  }

  @AfterAll
  public void afterClass() {
    mockServer.stop();
  }

  @BeforeEach
  void setUp() {

    // Give us better logging support, borrowed from SO
    ExchangeStrategies exchangeStrategies = ExchangeStrategies.withDefaults();
    exchangeStrategies
      .messageWriters().stream()
      .filter(LoggingCodecSupport.class::isInstance)
      .forEach(writer -> ((LoggingCodecSupport)writer).setEnableLoggingRequestDetails(true));

    webClient = WebTestClient
      .bindToServer()
      .exchangeStrategies(exchangeStrategies)
      .build();

  }

  @Test
  void whenAuthorizationApprovedThenTokenAvailable() throws IOException, InterruptedException {

    // Given client registered with valid settings
    givenClientRegisteredWithSettings(TwitchAuthConnectionSettings.builder()
      .host(twitchBaseUrl)
      .authorizationPath(twitchAuthPath)
      .tokenPath(twitchTokenPath)
      .scopes(scopes)
      .clientId(clientId)
      .clientSecret(clientSecret)
      .redirectUri(redirectUri)
      .build()
    );

    // And Given Twitch will respond with a token
    createTokenExchangeExpectations(twitchPort, twitchTokenPath, accessToken, expiresIn, refreshToken, scopes);

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
    when(authorizationRequestRepository.loadAuthorizationRequest(any()))
      .thenReturn(authorizationRequest);
    when(authorizationRequestRepository.removeAuthorizationRequest(any(), any()))
      .thenReturn(authorizationRequest);

    // (but no authorized client yet)
    assertThat((OAuth2AuthorizedClient) authorizedClientService
      .loadAuthorizedClient("twitch", User.SYSTEM_PRINCIPAL_NAME)
    ).isNull();

    // When we are redirected back to our server
    String uri = redirectUri + "?code={code}&scope={scope}&state={state}";
    Map<String, String> uriParams = new HashMap<>();
    uriParams.put("code", code);
    uriParams.put("state", state);
    uriParams.put("scope", scopes.stream()
      .map(it -> {
        try {
          return URLEncoder.encode(it, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
          return null;
        }
      })
      .collect(Collectors.joining("+"))
    );
    webClient
      .get()
      .uri(uri, uriParams)
      .exchange();

    // Then it performs token exchange and we can verify this was successful
    OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient("twitch",
      User.SYSTEM_PRINCIPAL_NAME);

    assertThat(authorizedClient).isNotNull();
    assertThat(authorizedClient.getAccessToken().getTokenValue()).isEqualTo(accessToken);
    assertThat(authorizedClient.getRefreshToken().getTokenValue()).isEqualTo(refreshToken);
//    assertThat(authorizedClient.getAccessToken().getScopes()).containsExactlyInAnyOrderElementsOf(scopes); // TODO create modified HttpMessageConverter; Spring expects a String, not a JSON array

  }

  private void givenClientRegisteredWithSettings(TwitchAuthConnectionSettings settings) {
    when(twitchAuthConnectionSettingsFactory.getSettings()).thenReturn(settings);
  }

  private void createTokenExchangeExpectations(
    int twitchPort,
    String twitchTokenPath,
    String accessToken,
    int expiresIn,
    String refreshToken,
    List<String> scopes
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
          .withBody(json(createTokenJson(accessToken, expiresIn, refreshToken, scopes)))
      );
  }

  private String createTokenJson(String accessToken, int expiresIn, String refreshToken, List<String> scopes) {
    return "{" + System.lineSeparator()
      + "\"access_token\": \"" + accessToken + "\"," + System.lineSeparator()
      + "\"expires_in\": " + expiresIn + "," + System.lineSeparator()
      + "\"refresh_token\": \"" + refreshToken + "\"," + System.lineSeparator()
      + "\"scope\": [" + scopes.stream().map(it -> "\"" + it + "\"").collect(Collectors.joining(",")) + "]," + System.lineSeparator()
      + "\"token_type\": \"" + "bearer" + "\"" + System.lineSeparator()
      + "}";
  }
}
