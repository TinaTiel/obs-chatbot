package com.tinatiel.obschatbot.core.client.twitch.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;
import com.tinatiel.obschatbot.core.request.handler.chat.ChatRequestHandler;
import com.tinatiel.obschatbot.data.client.twitch.chat.TwitchClientChatDataService;
import com.tinatiel.obschatbot.data.client.twitch.chat.model.TwitchClientChatDataDto;
import com.tinatiel.obschatbot.security.owner.OwnerDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@EnableConfigurationProperties
@TestPropertySource(properties = {
  "com.tinatiel.twitch.chat.irc-host=foo",
  "com.tinatiel.twitch.chat.irc-port=1234",
})
@ContextConfiguration(classes = {
  TwitchChatClientConfig.class,
  TwitchChatClientSettingsFactory.class
})
@SpringJUnitConfig
public class TwitchChatClientSettingsFactoryTest {

  @MockBean
  OwnerService ownerService;

  @MockBean
  TwitchClientChatDataService dataService;

  @MockBean
  ChatRequestHandler chatRequestHandler; // ignored
  @MockBean
  OAuth2AuthorizedClientService authorizedClientService; // ignored

  @Autowired
  TwitchChatClientSettingsFactory twitchChatClientSettingsFactory;

  @Test
  void retrieveExpectedSettings() {

    // Given an owner and data are retrieved
    OwnerDto ownerDto = OwnerDto.builder()
      .name("someowner")
      .id(UUID.randomUUID())
      .build();
    TwitchClientChatDataDto dataDto = TwitchClientChatDataDto.builder()
      .owner(ownerDto.getId())
      .broadcasterChannelUsername("80snerd")
      .connectionAttempts(78)
      .connectionTimeoutMs(1234)
      .trigger("|")
      .parseEntireMessage(true)
      .joinMessage("🤖 I'm alive! 🤖")
      .leaveMessage("Asta la vista! 👍")
      .build();
    when(ownerService.getOwner()).thenReturn(ownerDto);
    when(dataService.findByOwner(any())).thenReturn(Optional.of(dataDto));

    // When the factory retrieves its settings
    TwitchChatClientSettings settings = twitchChatClientSettingsFactory.getSettings();

    // Then it contains the expected values
    TwitchChatClientSettings expectedSettings = TwitchChatClientSettings.builder()
      .ircHost("foo")
      .ircPort(1234)
      .broadcasterAccountUsername(dataDto.getBroadcasterChannelUsername())
      .connectionAttempts(dataDto.getConnectionAttempts())
      .connectionTimeoutMs(dataDto.getConnectionTimeoutMs())
      .joinMessage(dataDto.getJoinMessage())
      .leaveMessage(dataDto.getLeaveMessage())
      .trigger(dataDto.getTrigger())
      .parseEntireMessage(dataDto.isParseEntireMessage())
      .build();
    assertThat(settings).usingRecursiveComparison().isEqualTo(expectedSettings);

  }

  @Test
  void noSettings() {

    // Given no data is retrieved
    when(ownerService.getOwner()).thenReturn(mock(OwnerDto.class, RETURNS_DEEP_STUBS));
    when(dataService.findByOwner(any())).thenReturn(Optional.empty());

    // When the factory retrieves its settings
    TwitchChatClientSettings settings = twitchChatClientSettingsFactory.getSettings();

    // Then it contains only the properties from the environment
    // (defaults are set in the DB, not logically)
    TwitchChatClientSettings expectedSettings = TwitchChatClientSettings.builder()
      .ircHost("foo")
      .ircPort(1234)
      .build();
    assertThat(settings).usingRecursiveComparison().isEqualTo(expectedSettings);
  }

}
