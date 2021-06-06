package com.tinatiel.obschatbot.data.client.twitch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tinatiel.obschatbot.data.client.twitch.entity.TwitchClientDataEntity;
import com.tinatiel.obschatbot.data.client.twitch.entity.TwitchClientDataRepository;
import com.tinatiel.obschatbot.data.client.twitch.model.TwitchClientDataDto;
import com.tinatiel.obschatbot.data.common.CommonConfig;
import com.tinatiel.obschatbot.data.error.DataPersistenceException;
import com.tinatiel.obschatbot.data.system.entity.SystemSettingsEntity;
import com.tinatiel.obschatbot.data.system.model.SystemSettingsDto;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
  CommonConfig.class,
  TwitchClientDataConfig.class,
  TwitchClientDataServiceTest.TestConfig.class})
@DataJpaTest
public class TwitchClientDataServiceTest {

  @EnableJpaRepositories(basePackages = "com.tinatiel.obschatbot.data.client.twitch")
  @Configuration
  static class TestConfig {

  }

  @Autowired
  TwitchClientDataRepository twitchClientDataRepository;

  @Autowired
  TwitchClientDataService twitchClientDataService;

  TwitchClientDataEntity existingTwitchClientData;

  @BeforeEach
  void setUp() {
    twitchClientDataRepository.deleteAll();
    TwitchClientDataEntity existing = new TwitchClientDataEntity();
    existing.setOwner(UUID.randomUUID());
    existing.setClientId("clientid");
    existing.setClientSecret("clientsecret");
    existing.setBotAccountUsername("botaccount");
    existing.setBroadcasterChannelUsername("broadcasterchannel");
    existing.setConnectionAttempts(42);
    existing.setConnectionTimeoutMs(6969);

    existingTwitchClientData = twitchClientDataRepository.saveAndFlush(existing);
    assertThat(existingTwitchClientData.getOwner()).isNotNull();
  }

  @Test
  void getExistingSettings() {

    // Given settings exist
    assertThat(twitchClientDataRepository.count()).isEqualTo(1);

    // When called
    Optional<TwitchClientDataDto> actual = twitchClientDataService.findByOwner(
      existingTwitchClientData.getOwner());

    // Then the expected DTO is returned
    assertThat(actual).isPresent();
    TwitchClientDataDto expected = TwitchClientDataDto.builder()
      .owner(existingTwitchClientData.getOwner())
      .clientId(existingTwitchClientData.getClientId())
      .clientSecret(existingTwitchClientData.getClientSecret())
      .botAccountUsername(existingTwitchClientData.getBotAccountUsername())
      .broadcasterChannelUsername(existingTwitchClientData.getBroadcasterChannelUsername())
      .connectionAttempts(existingTwitchClientData.getConnectionAttempts())
      .connectionTimeoutMs(existingTwitchClientData.getConnectionTimeoutMs())
      .build();
    assertThat(actual.get()).usingRecursiveComparison().isEqualTo(expected);

  }

  @Test
  void saveRetrieveNewSettings() {

    // Given a request for new settings
    TwitchClientDataDto request = TwitchClientDataDto.builder()
      .owner(UUID.randomUUID())
      .clientId("whehehehehe")
      .clientSecret("shhhhhh")
      .botAccountUsername("mrdata")
      .broadcasterChannelUsername("HIEVERYONE")
      .connectionAttempts(1234)
      .connectionTimeoutMs(9876)
      .build();

    // When saved
    TwitchClientDataDto result = twitchClientDataService.save(request);

    // Then record count has increased
    assertThat(twitchClientDataRepository.count()).isEqualTo(2);

    // And it matches as expected
    TwitchClientDataDto retrieved = twitchClientDataService.findByOwner(request.getOwner()).get();
    assertThat(result).usingRecursiveComparison().isEqualTo(request);
    assertThat(result).usingRecursiveComparison().isEqualTo(retrieved);

  }

  @Test
  void nullOwner() {
    assertThatThrownBy(() -> {
      twitchClientDataService.save(TwitchClientDataDto.builder()
        .build());
    }).isInstanceOf(DataPersistenceException.class);
  }

  @Test
  void ownerOnlyAllowedOneSettingsSet() {

    // Given a setting is saved for an existing owner succeeds
    twitchClientDataService.save(TwitchClientDataDto.builder()
      .owner(existingTwitchClientData.getOwner())
      .build());

    // Then the count is the same as before
    assertThat(twitchClientDataRepository.count()).isEqualTo(1);

  }

  @Test
  void updateExistingSettings() {

    // Given settings exist
    assertThat(twitchClientDataRepository.count()).isEqualTo(1);

    // When updated
    TwitchClientDataDto request = TwitchClientDataDto.builder()
      .owner(existingTwitchClientData.getOwner())
      .clientId("whehehehehe")
      .clientSecret("shhhhhh")
      .botAccountUsername("mrdata")
      .broadcasterChannelUsername("HIEVERYONE")
      .connectionAttempts(1234)
      .connectionTimeoutMs(9876)
      .build();
    TwitchClientDataDto result = twitchClientDataService.save(request);

    // Then the record count is the same
    assertThat(twitchClientDataRepository.count()).isEqualTo(1);

    // And the expected DTO is returned
    TwitchClientDataDto retrieved = twitchClientDataService.findByOwner(request.getOwner()).get();
    assertThat(result).usingRecursiveComparison().isEqualTo(request);
    assertThat(result).usingRecursiveComparison().isEqualTo(retrieved);

  }

}
