package com.tinatiel.obschatbot.data.load;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.data.client.obs.ObsClientDataService;
import com.tinatiel.obschatbot.data.client.obs.model.ObsClientSettingsDto;
import com.tinatiel.obschatbot.data.client.twitch.auth.TwitchClientAuthDataService;
import com.tinatiel.obschatbot.data.client.twitch.auth.model.TwitchClientAuthDataDto;
import com.tinatiel.obschatbot.data.client.twitch.chat.TwitchClientChatDataService;
import com.tinatiel.obschatbot.data.client.twitch.chat.model.TwitchClientChatDataDto;
import com.tinatiel.obschatbot.data.system.SystemSettingsDataService;
import com.tinatiel.obschatbot.security.owner.OwnerDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DataLoaderTest {

  private OwnerDto ownerDto = OwnerDto.builder().id(UUID.randomUUID()).name("owner").build();
  @Mock OwnerService ownerService;
  @Mock ObsClientDataService obsClientDataService;
  @Mock TwitchClientChatDataService twitchClientChatDataService;
  @Mock TwitchClientAuthDataService twitchClientAuthDataService;
  @Mock SystemSettingsDataService systemSettingsDataService;

  @InjectMocks
  DataLoaderImpl dataLoader;

  @BeforeEach
  void setUp() {
    when(ownerService.getOwner()).thenReturn(ownerDto);
  }

  @Test
  void loadNewObsSettings() {

    // given no existing data is found
    assertThat(obsClientDataService.findByOwner(ownerDto.getId())).isEmpty();

    // when loaded
    dataLoader.loadObsSettings();

    // then the expected data is loaded
    ArgumentCaptor<ObsClientSettingsDto> captor = ArgumentCaptor.forClass(ObsClientSettingsDto.class);
    verify(obsClientDataService).save(captor.capture());
    ObsClientSettingsDto actual = captor.getValue();
    ObsClientSettingsDto expected = ObsClientSettingsDto.builder()
      .owner(ownerDto.getId())
      .host("localhost")
      .port(4444)
      .password(null)
      .connectionTimeoutMs(5000)
      .build();

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);

  }

  @Test
  void loadExistingObsSettings() {

    // given existing data is found
    ObsClientSettingsDto expected = ObsClientSettingsDto.builder()
      .owner(ownerDto.getId())
      .build();
    when(obsClientDataService.findByOwner(eq(ownerDto.getId()))).thenReturn(Optional.of(expected));

    // when loaded
    dataLoader.loadObsSettings();

    // then no data is saved
    verify(obsClientDataService, never()).save(any());

  }

  @Test
  void loadNewTwitchChatSettings() {

    // given no existing data is found
    assertThat(twitchClientChatDataService.findByOwner(ownerDto.getId())).isEmpty();

    // when loaded
    dataLoader.loadTwitchChatSettings();

    // then the expected data is loaded
    ArgumentCaptor<TwitchClientChatDataDto> captor = ArgumentCaptor.forClass(TwitchClientChatDataDto.class);
    verify(twitchClientChatDataService).save(captor.capture());
    TwitchClientChatDataDto actual = captor.getValue();
    TwitchClientChatDataDto expected = TwitchClientChatDataDto.builder()
      .owner(ownerDto.getId())
      .broadcasterChannelUsername(null)
      .trigger("!")
      .parseEntireMessage(false)
      .joinMessage("OBS Chatbot has joined!")
      .leaveMessage("Obs Chatbot is leaving...")
      .connectionAttempts(3)
      .connectionTimeoutMs(10000)
      .build();

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);

  }

  @Test
  void loadExistingTwitchChatSettings() {

    // given existing data is found
    TwitchClientChatDataDto expected = TwitchClientChatDataDto.builder()
      .owner(ownerDto.getId())
      .build();
    when(twitchClientChatDataService.findByOwner(eq(ownerDto.getId()))).thenReturn(Optional.of(expected));

    // when loaded
    dataLoader.loadTwitchChatSettings();

    // then no data is saved
    verify(twitchClientChatDataService, never()).save(any());

  }

  @Test
  void loadNewTwitchAuthSettings() {

    // given no existing data is found
    assertThat(twitchClientAuthDataService.findByOwner(ownerDto.getId())).isEmpty();

    // when loaded
    dataLoader.loadTwitchAuthSettings();

    // then the expected data is loaded
    ArgumentCaptor<TwitchClientAuthDataDto> captor = ArgumentCaptor.forClass(TwitchClientAuthDataDto.class);
    verify(twitchClientAuthDataService).save(captor.capture());
    TwitchClientAuthDataDto actual = captor.getValue();
    TwitchClientAuthDataDto expected = TwitchClientAuthDataDto.builder()
      .owner(ownerDto.getId())
      .clientId(null)
      .clientSecret(null)
      .build();

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);

  }

  @Test
  void loadExistingTwitchAuthSettings() {

    // given existing data is found
    TwitchClientAuthDataDto expected = TwitchClientAuthDataDto.builder()
      .owner(ownerDto.getId())
      .build();
    when(twitchClientAuthDataService.findByOwner(eq(ownerDto.getId()))).thenReturn(Optional.of(expected));

    // when loaded
    dataLoader.loadTwitchAuthSettings();

    // then no data is saved
    verify(twitchClientAuthDataService, never()).save(any());

  }

}
