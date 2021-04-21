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
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.test.web.reactive.server.WebTestClient;

public class TwitchAuthTests extends AbstractTwitchAuthTest {

  @MockBean
  TwitchAuthConnectionSettingsFactory mockTwitchAuthConnSettingsFactory;

  @MockBean
  AuthorizationRequestRepository<OAuth2AuthorizationRequest> mockAuthorizationRequestRepository;

  @Autowired
  OAuth2AuthorizedClientService authorizedClientService;

  @Autowired
  TwitchAuthScheduler twitchAuthScheduler;

  private WebTestClient webClient;

  @BeforeEach
  void setUp() {

    // Give us better logging support, borrowed from SO
    webClient = buildLoggingWebClient();

  }

  @Test
  void whenAuthorizationApprovedThenTokenAvailable() throws IOException, InterruptedException {

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

    // And our token expires in a reasonable time during this test
    int expiresIn = 2; // seconds

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

    // And when we wait for the token to expire
    Thread.sleep(2000);

    // Given twitch will refresh our token
    String newAccessToken = "newAccessToken";
    String newRefreshToken = "newRefreshToken";
    givenTwitchRefreshesToken(twitchPort, twitchTokenPath, newAccessToken, expiresIn, newRefreshToken, scopes);

    // When we refresh our token
    twitchAuthScheduler.refreshTokenIfNeeded();

    // Then we have a new token
    OAuth2AuthorizedClient newAuthorizedClient = authorizedClientService.loadAuthorizedClient("twitch",
      User.SYSTEM_PRINCIPAL_NAME);
    assertThat(newAuthorizedClient).isNotNull();
    assertThat(newAuthorizedClient.getAccessToken().getTokenValue()).isEqualTo(newAccessToken);
    assertThat(newAuthorizedClient.getRefreshToken().getTokenValue()).isEqualTo(newRefreshToken);

  }

}
