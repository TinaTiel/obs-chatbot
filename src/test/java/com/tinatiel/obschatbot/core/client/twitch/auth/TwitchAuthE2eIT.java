package com.tinatiel.obschatbot.core.client.twitch.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.core.user.User;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

@DirtiesContext // Because we have to use the defined port, aaaaaa >:(
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT, properties = {"server.port=8080"})
public class TwitchAuthE2eIT {

  int localPort = 8080;

  public static MockWebServer twitchServer;
  TwitchAuthConnectionSettings authSettings;

  @MockBean
  TwitchAuthConnectionSettingsFactory authSettingsFactory;

  @Autowired
  TwitchAuthClient twitchAuthClient;

  @MockBean
  AuthorizationRequestRepository<OAuth2AuthorizationRequest> mockAuthorizationRequestRepository;

  @Autowired
  OAuth2AuthorizedClientService authorizedClientService;

  @Autowired
  TwitchAuthValidationService twitchAuthValidationService;

  private WebTestClient webClient;

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
    String thisHost = "http://localhost:" + localPort;
    String twitchHost = "http://localhost:" + twitchServer.getPort();
    authSettings = TwitchAuthConnectionSettings.builder()
      .host(twitchHost)
      .clientId("clientId")
      .clientSecret("clientSecret")
      .redirectUri(thisHost + "/foo/bar")
      .authorizationPath("/somepath/authorize")
      .tokenPath("/somepath/token")
      .validationPath("/somepath/validate")
      .scopes(Arrays.asList("foo:bar", "chat:read", "channel:moderate"))
      .build();
    when(authSettingsFactory.getSettings()).thenReturn(authSettings);

    // Setup the web client
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

    // Given our token expires in a reasonable time during this test
    int expiresIn = 2; // seconds

    // And Given Twitch will respond with a token
    String accessToken = "someaccesstoken";
    String refreshToken = "somerefreshtoken";
    givenTwitchRespondsWithTokens(accessToken, expiresIn, refreshToken, authSettings.getScopes());

    // Given we have initiated an authorization request
    String state = "somestate1234";
    givenAnAuthorizationRequestExists(
      mockAuthorizationRequestRepository,
      authSettings.getHost(),
      authSettings.getAuthorizationPath(),
      authSettings.getScopes(),
      authSettings.getClientId(),
      authSettings.getRedirectUri(),
      state
    );

    // (but no authorized client yet)
    assertThat((OAuth2AuthorizedClient) authorizedClientService
      .loadAuthorizedClient("twitch", User.SYSTEM_PRINCIPAL_NAME)
    ).isNull();

    // When we are redirected back to our server
    String code = "somecode";
    String uri = authSettings.getRedirectUri() + "?code={code}&scope={scope}&state={state}";
    Map<String, String> uriParams = new HashMap<>();
    uriParams.put("code", code);
    uriParams.put("state", state);
    uriParams.put("scope", authSettings.getScopes().stream()
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

    // Then it performs token exchange (via code)
    String decodedBody = URLDecoder.decode(twitchServer.takeRequest().getBody().readUtf8(), StandardCharsets.UTF_8);
    assertThat(decodedBody)
      .contains("grant_type=authorization_code")
      .contains("code=" + code)
      .contains("client_id=" + authSettings.getClientId())
      .contains("client_secret=" + authSettings.getClientSecret())
      .contains("redirect_uri=" + authSettings.getRedirectUri());

    // And we can verify the token was stored successfully
    OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient("twitch",
      User.SYSTEM_PRINCIPAL_NAME);
    assertThat(authorizedClient).isNotNull();
    assertThat(authorizedClient.getAccessToken().getTokenValue()).isEqualTo(accessToken);
    assertThat(authorizedClient.getRefreshToken().getTokenValue()).isEqualTo(refreshToken);

    // And when we wait for the token to expire
    Thread.sleep(2000);

    // Given twitch will refresh our token
    String newAccessToken = "newAccessToken";
    String newRefreshToken = "newRefreshToken";
    givenTwitchRefreshesToken(newAccessToken, expiresIn, newRefreshToken, authSettings.getScopes());

    // When we refresh our token
    twitchAuthValidationService.refreshTokenIfNeeded();

    // Then we have a new token
    OAuth2AuthorizedClient newAuthorizedClient = authorizedClientService.loadAuthorizedClient("twitch",
      User.SYSTEM_PRINCIPAL_NAME);
    assertThat(newAuthorizedClient).isNotNull();
    assertThat(newAuthorizedClient.getAccessToken().getTokenValue()).isEqualTo(newAccessToken);
    assertThat(newAuthorizedClient.getRefreshToken().getTokenValue()).isEqualTo(newRefreshToken);

  }

  void givenTwitchRefreshesToken(
    String accessToken,
    int expiresIn,
    String refreshToken,
    List<String> scopes
  ) {
    givenTwitchRespondsWithTokens(
      accessToken,
      expiresIn,
      refreshToken,
      scopes
    );
  }

  void givenTwitchRespondsWithTokens(
    String accessToken,
    int expiresIn,
    String refreshToken,
    List<String> scopes
  ) {
    twitchServer.enqueue(new MockResponse()
      .setResponseCode(200)
      .setHeader("Content-Type", "application/json")
      .setBody(createTokenJson(accessToken, expiresIn, refreshToken, scopes))
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

}
