package com.tinatiel.obschatbot.core.client.twitch.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;
import com.tinatiel.obschatbot.security.owner.OwnerDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import javax.net.ssl.SSLSocketFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.pircbotx.PircBotX;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class TwitchChatClientFactoryTest {

  @Mock
  OwnerService ownerService;

  @Mock
  OAuth2AuthorizedClientService authorizedClientService;

  @Mock
  ClientSettingsFactory<TwitchChatClientSettings> clientSettingsFactory;

  @Mock
  SSLSocketFactory sslSocketFactory;

  @Mock
  PircBotxListener pircBotxListener;

  @InjectMocks
  TwitchChatClientFactory twitchChatClientFactory;

  @Test
  void retrieveWithExpectedSettings() {

    // Given we are authorized
    when(ownerService.getOwner()).thenReturn(mock(OwnerDto.class, RETURNS_DEEP_STUBS));
    OAuth2AuthorizedClient oAuth2AuthorizedClient = mock(OAuth2AuthorizedClient.class);
    OAuth2AccessToken accessToken = mock(OAuth2AccessToken.class);
    when(authorizedClientService.loadAuthorizedClient(eq("twitch"), any())).thenReturn(oAuth2AuthorizedClient);
    when(oAuth2AuthorizedClient.getAccessToken()).thenReturn(accessToken);
    when(accessToken.getTokenValue()).thenReturn("sometoken");

    // And given the expected settings are returned
    TwitchChatClientSettings settings = TwitchChatClientSettings.builder()
      .ircHost("localhost")
      .ircPort(1234)
      .connectionAttempts(69)
      .connectionTimeoutMs(9876)
      .build();
    when(clientSettingsFactory.getSettings()).thenReturn(settings);

    // When a client is retrieved
    TwitchChatClientDelegate delegate = twitchChatClientFactory.generate();

    // Then it has the expected settings
    assertThat(delegate.getSettings()).usingRecursiveComparison().isEqualTo(settings);

    // And the client instance has the expected values
    PircBotX client = delegate.getClient();
    assertThat(client.getConfiguration().getServers().get(0).getHostname()).isEqualTo(settings.getIrcHost());
    assertThat(client.getConfiguration().getServers().get(0).getPort()).isEqualTo(settings.getIrcPort());
    assertThat(client.getConfiguration().getAutoJoinChannels()).containsKey("#" + settings.getBroadcasterAccountUsername());
    assertThat(client.getConfiguration().getName()).isEqualTo("ignoreme"); // required, but not used
    assertThat(client.getConfiguration().getServerPassword()).isEqualTo("oauth:sometoken");
    assertThat(client.getConfiguration().getListenerManager().getListeners()).contains(pircBotxListener);
    assertThat(client.getConfiguration().isOnJoinWhoEnabled()).isFalse(); // WHO not supported by Twitch
    assertThat(client.getConfiguration().getSocketFactory()).isEqualTo(sslSocketFactory);
    assertThat(client.getConfiguration().getAutoReconnectAttempts()).isEqualTo(settings.getConnectionAttempts());
    assertThat(client.getConfiguration().getSocketConnectTimeout()).isEqualTo(settings.getConnectionTimeoutMs());

  }

}
