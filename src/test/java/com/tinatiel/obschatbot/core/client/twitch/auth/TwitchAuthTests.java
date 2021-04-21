package com.tinatiel.obschatbot.core.client.twitch.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.tinatiel.obschatbot.core.user.User;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

public class TwitchAuthTests extends AbstractTwitchAuthTest {

  // Define our info to initialize the MockServer expectations
  private String redirectUri = "http://localhost:" + localPort + "/foo"; // doesn't matter because Spring Security uses filters to detect
  private String twitchBaseUrl = "http://localhost:" + twitchPort;
  private String twitchAuthPath = "/somepath/authorize";
  private String twitchTokenPath = "/somepath/token";
  private List<String> scopes = Arrays.asList("foo:bar", "chat:read", "channel:moderate");
  private String clientId = "myclientid";
  private String clientSecret = "myclientsecret";
  private String code = "abcd1234";
  private String state = "asdfasdf";
  private String accessToken = "myaccesstoken";
  private String refreshToken = "myrefreshtoken";
  private int expiresIn = 123456789;


  private WebTestClient webClient;

  @MockBean
  TwitchAuthConnectionSettingsFactory mockTwitchAuthConnSettingsFactory;

  @MockBean
  AuthorizationRequestRepository<OAuth2AuthorizationRequest> mockAuthorizationRequestRepository;

  @Autowired
  OAuth2AuthorizedClientService authorizedClientService;

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
    givenClientRegisteredWithSettings(mockTwitchAuthConnSettingsFactory, TwitchAuthConnectionSettings.builder()
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
    givenTwitchRespondsWithToken(twitchPort, twitchTokenPath, accessToken, expiresIn, refreshToken, scopes);

    // Given we have initiated an authorization request
    givenAnAuthorizationRequestExists(
      mockAuthorizationRequestRepository,
      twitchBaseUrl,
      twitchAuthPath,
      scopes,
      clientId,
      redirectUri,
      state
    );

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

  }

  @Test
  void refreshingTokenSucceeds() {

    // Given we have a successful authentication

    // And it expires

    // When we refresh it then a new token is retrieved


  }


}
