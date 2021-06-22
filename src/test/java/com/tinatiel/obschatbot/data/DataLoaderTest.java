package com.tinatiel.obschatbot.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.data.client.obs.ObsClientDataConfig;
import com.tinatiel.obschatbot.data.client.obs.ObsClientDataService;
import com.tinatiel.obschatbot.data.client.obs.entity.ObsClientDataRepository;
import com.tinatiel.obschatbot.data.client.obs.model.ObsClientSettingsDto;
import com.tinatiel.obschatbot.data.client.twitch.auth.TwitchClientAuthDataConfig;
import com.tinatiel.obschatbot.data.client.twitch.auth.TwitchClientAuthDataService;
import com.tinatiel.obschatbot.data.client.twitch.auth.entity.TwitchClientAuthDataRepository;
import com.tinatiel.obschatbot.data.client.twitch.chat.TwitchClientChatDataConfig;
import com.tinatiel.obschatbot.data.client.twitch.chat.TwitchClientChatDataService;
import com.tinatiel.obschatbot.data.client.twitch.chat.entity.TwitchClientChatDataRepository;
import com.tinatiel.obschatbot.data.common.CommonConfig;
import com.tinatiel.obschatbot.data.load.DataLoaderImpl;
import com.tinatiel.obschatbot.data.system.SystemDataConfig;
import com.tinatiel.obschatbot.data.system.SystemSettingsDataService;
import com.tinatiel.obschatbot.data.system.entity.SystemSettingsRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

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

}
