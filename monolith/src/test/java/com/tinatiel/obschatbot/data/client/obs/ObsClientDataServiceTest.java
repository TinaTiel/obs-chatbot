package com.tinatiel.obschatbot.data.client.obs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tinatiel.obschatbot.data.client.obs.entity.ObsClientDataRepository;
import com.tinatiel.obschatbot.data.client.obs.entity.ObsClientSettingsEntity;
import com.tinatiel.obschatbot.data.client.obs.model.ObsClientSettingsDto;
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
  ObsClientDataConfig.class,
  ObsClientDataServiceTest.TestConfig.class})
@DataJpaTest
public class ObsClientDataServiceTest {

  @EnableJpaRepositories(basePackages = "com.tinatiel.obschatbot.data.client.obs")
  @Configuration
  static class TestConfig {

  }

  @Autowired
  ObsClientDataRepository obsClientDataRepository;

  @Autowired
  ObsClientDataService obsClientDataService;

  ObsClientSettingsEntity existingObsData;

  @BeforeEach
  void setUp() {
    obsClientDataRepository.deleteAll();
    ObsClientSettingsEntity existing = new ObsClientSettingsEntity();
    existing.setOwner(UUID.randomUUID());
    existing.setHost("localhost");
    existing.setPort(1234);
    existing.setPassword("correcthorsebatterystaple");
    existing.setConnectionTimeoutMs(4567);

    existingObsData = obsClientDataRepository.saveAndFlush(existing);
    assertThat(existingObsData.getOwner()).isNotNull();
  }

  @Test
  void getExistingSettings() {

    // Given settings exist
    assertThat(obsClientDataRepository.count()).isEqualTo(1);

    // When called
    Optional<ObsClientSettingsDto> actual = obsClientDataService.findByOwner(
      existingObsData.getOwner());

    // Then the expected DTO is returned
    assertThat(actual).isPresent();
    ObsClientSettingsDto expected = ObsClientSettingsDto.builder()
      .owner(existingObsData.getOwner())
      .host(existingObsData.getHost())
      .port(existingObsData.getPort())
      .password(existingObsData.getPassword())
      .connectionTimeoutMs(existingObsData.getConnectionTimeoutMs())
      .build();
    assertThat(actual.get()).usingRecursiveComparison().isEqualTo(expected);

  }

  @Test
  void saveRetrieveNewSettings() {

    // Given a request for new settings
    ObsClientSettingsDto request = ObsClientSettingsDto.builder()
      .owner(UUID.randomUUID())
      .host("localboast")
      .port(6969)
      .password("yeehaw")
      .connectionTimeoutMs(7799)
      .build();

    // When saved
    ObsClientSettingsDto result = obsClientDataService.save(request);

    // Then record count has increased
    assertThat(obsClientDataRepository.count()).isEqualTo(2);

    // And it matches as expected
    ObsClientSettingsDto retrieved = obsClientDataService.findByOwner(request.getOwner()).get();
    assertThat(result).usingRecursiveComparison().isEqualTo(request);
    assertThat(result).usingRecursiveComparison().isEqualTo(retrieved);

  }

  @Test
  void nullOwner() {
    assertThatThrownBy(() -> {
      obsClientDataService.save(ObsClientSettingsDto.builder()
        .build());
    }).isInstanceOf(DataPersistenceException.class);
  }

  @Test
  void ownerOnlyAllowedOneSettingsSet() {

    // Given a setting is saved for an existing owner succeeds
    obsClientDataService.save(ObsClientSettingsDto.builder()
      .owner(existingObsData.getOwner())
      .build());

    // Then the count is the same as before
    assertThat(obsClientDataRepository.count()).isEqualTo(1);

  }

  @Test
  void updateExistingSettings() {

    // Given settings exist
    assertThat(obsClientDataRepository.count()).isEqualTo(1);

    // When updated
    ObsClientSettingsDto request = ObsClientSettingsDto.builder()
      .owner(existingObsData.getOwner())
      .host("localboast")
      .port(6969)
      .password("yeehaw")
      .connectionTimeoutMs(7799)
      .build();
    ObsClientSettingsDto result = obsClientDataService.save(request);

    // Then the record count is the same
    assertThat(obsClientDataRepository.count()).isEqualTo(1);

    // And the expected DTO is returned
    ObsClientSettingsDto retrieved = obsClientDataService.findByOwner(request.getOwner()).get();
    assertThat(result).usingRecursiveComparison().isEqualTo(request);
    assertThat(result).usingRecursiveComparison().isEqualTo(retrieved);

  }

}
