package com.tinatiel.obschatbot.data.client.twitch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tinatiel.obschatbot.data.client.twitch.auth.TwitchClientAuthDataConfig;
import com.tinatiel.obschatbot.data.client.twitch.auth.TwitchClientAuthDataService;
import com.tinatiel.obschatbot.data.client.twitch.auth.entity.TwitchClientAuthDataEntity;
import com.tinatiel.obschatbot.data.client.twitch.auth.entity.TwitchClientAuthDataRepository;
import com.tinatiel.obschatbot.data.client.twitch.auth.model.TwitchClientAuthDataDto;
import com.tinatiel.obschatbot.data.common.CommonConfig;
import com.tinatiel.obschatbot.data.error.DataPersistenceException;
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
  TwitchClientAuthDataConfig.class,
  TwitchClientAuthDataServiceTest.TestConfig.class})
@DataJpaTest
public class TwitchClientAuthDataServiceTest {

  @EnableJpaRepositories(basePackages = "com.tinatiel.obschatbot.data.client.twitch.auth")
  @Configuration
  static class TestConfig {

  }

  @Autowired
  TwitchClientAuthDataRepository twitchClientAuthDataRepository;

  @Autowired
  TwitchClientAuthDataService twitchClientAuthDataService;

  TwitchClientAuthDataEntity existingTwitchClientData;

  @BeforeEach
  void setUp() {
    twitchClientAuthDataRepository.deleteAll();
    TwitchClientAuthDataEntity existing = new TwitchClientAuthDataEntity();
    existing.setOwner(UUID.randomUUID());
    existing.setClientId("clientid");
    existing.setClientSecret("clientsecret");

    existingTwitchClientData = twitchClientAuthDataRepository.saveAndFlush(existing);
    assertThat(existingTwitchClientData.getOwner()).isNotNull();
  }

  @Test
  void getExistingSettings() {

    // Given settings exist
    assertThat(twitchClientAuthDataRepository.count()).isEqualTo(1);

    // When called
    Optional<TwitchClientAuthDataDto> actual = twitchClientAuthDataService.findByOwner(
      existingTwitchClientData.getOwner());

    // Then the expected DTO is returned
    assertThat(actual).isPresent();
    TwitchClientAuthDataDto expected = TwitchClientAuthDataDto.builder()
      .owner(existingTwitchClientData.getOwner())
      .clientId(existingTwitchClientData.getClientId())
      .clientSecret(existingTwitchClientData.getClientSecret())
      .build();
    assertThat(actual.get()).usingRecursiveComparison().isEqualTo(expected);

  }

  @Test
  void saveRetrieveNewSettings() {

    // Given a request for new settings
    TwitchClientAuthDataDto request = TwitchClientAuthDataDto.builder()
      .owner(UUID.randomUUID())
      .clientId("whehehehehe")
      .clientSecret("shhhhhh")
      .build();

    // When saved
    TwitchClientAuthDataDto result = twitchClientAuthDataService.save(request);

    // Then record count has increased
    assertThat(twitchClientAuthDataRepository.count()).isEqualTo(2);

    // And it matches as expected
    TwitchClientAuthDataDto retrieved = twitchClientAuthDataService.findByOwner(request.getOwner()).get();
    assertThat(result).usingRecursiveComparison().isEqualTo(request);
    assertThat(result).usingRecursiveComparison().isEqualTo(retrieved);

  }

  @Test
  void nullOwner() {
    assertThatThrownBy(() -> {
      twitchClientAuthDataService.save(TwitchClientAuthDataDto.builder()
        .build());
    }).isInstanceOf(DataPersistenceException.class);
  }

  @Test
  void ownerOnlyAllowedOneSettingsSet() {

    // Given a setting is saved for an existing owner succeeds
    twitchClientAuthDataService.save(TwitchClientAuthDataDto.builder()
      .owner(existingTwitchClientData.getOwner())
      .build());

    // Then the count is the same as before
    assertThat(twitchClientAuthDataRepository.count()).isEqualTo(1);

  }

  @Test
  void updateExistingSettings() {

    // Given settings exist
    assertThat(twitchClientAuthDataRepository.count()).isEqualTo(1);

    // When updated
    TwitchClientAuthDataDto request = TwitchClientAuthDataDto.builder()
      .owner(existingTwitchClientData.getOwner())
      .clientId("whehehehehe")
      .clientSecret("shhhhhh")
      .build();
    TwitchClientAuthDataDto result = twitchClientAuthDataService.save(request);

    // Then the record count is the same
    assertThat(twitchClientAuthDataRepository.count()).isEqualTo(1);

    // And the expected DTO is returned
    TwitchClientAuthDataDto retrieved = twitchClientAuthDataService.findByOwner(request.getOwner()).get();
    assertThat(result).usingRecursiveComparison().isEqualTo(request);
    assertThat(result).usingRecursiveComparison().isEqualTo(retrieved);

  }

}
