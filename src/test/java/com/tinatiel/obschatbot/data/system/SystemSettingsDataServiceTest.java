package com.tinatiel.obschatbot.data.system;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tinatiel.obschatbot.data.common.CommonConfig;
import com.tinatiel.obschatbot.data.error.DataPersistenceException;
import com.tinatiel.obschatbot.data.system.entity.SystemSettingsEntity;
import com.tinatiel.obschatbot.data.system.entity.SystemSettingsRepository;
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

@ContextConfiguration(classes = {CommonConfig.class, SystemDataConfig.class, SystemSettingsDataServiceTest.TestConfig.class})
@DataJpaTest
public class SystemSettingsDataServiceTest {

  @EnableJpaRepositories(basePackages = "com.tinatiel.obschatbot.data.system.entity")
  @Configuration
  static class TestConfig {

  }

  @Autowired
  SystemSettingsRepository systemSettingsRepository;

  @Autowired
  SystemSettingsDataService systemSettingsDataService;

  SystemSettingsEntity existingSystemSettings;

  @BeforeEach
  void setUp() {
    systemSettingsRepository.deleteAll();
    SystemSettingsEntity existing = new SystemSettingsEntity();
    existing.setOwner(UUID.randomUUID());
    existing.setRecursionTimeoutMillis(42);
    existing.setMaxActionBatchSize(69);
    existingSystemSettings = systemSettingsRepository.saveAndFlush(existing);
    assertThat(existingSystemSettings.getOwner()).isNotNull();
  }

  @Test
  void getExistingSettings() {

    // Given settings exist
    assertThat(systemSettingsRepository.count()).isEqualTo(1);

    // When called
    Optional<SystemSettingsDto> actual = systemSettingsDataService.findByOwner(existingSystemSettings.getOwner());

    // Then the expected DTO is returned
    assertThat(actual).isPresent();
    SystemSettingsDto expected = SystemSettingsDto.builder()
      .owner(existingSystemSettings.getOwner())
      .maxActionBatchSize(69)
      .recursionTimeoutMillis(42)
      .build();
    assertThat(actual.get()).usingRecursiveComparison().isEqualTo(expected);

  }

  @Test
  void saveRetrieveNewSettings() {

    // Given a request for new settings
    SystemSettingsDto request = SystemSettingsDto.builder()
      .owner(UUID.randomUUID())
      .maxActionBatchSize(156)
      .recursionTimeoutMillis(979797)
      .build();

    // When saved
    SystemSettingsDto result = systemSettingsDataService.save(request);

    // Then record count has increased
    assertThat(systemSettingsRepository.count()).isEqualTo(2);

    // And it matches as expected
    SystemSettingsDto retrieved = systemSettingsDataService.findByOwner(request.getOwner()).get();
    assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(request);
    assertThat(result).usingRecursiveComparison().isEqualTo(retrieved);

  }

  @Test
  void nullOwner() {
    assertThatThrownBy(() -> {
      systemSettingsDataService.save(SystemSettingsDto.builder()
        .build());
    }).isInstanceOf(DataPersistenceException.class);
  }

  @Test
  void ownerOnlyAllowedOneSettingsSet() {

    // Given a setting is saved for an existing owner succeeds
    systemSettingsDataService.save(SystemSettingsDto.builder()
      .owner(existingSystemSettings.getOwner())
      .build());

    // Then the count is the same as before
    assertThat(systemSettingsRepository.count()).isEqualTo(1);

  }

  @Test
  void updateExistingSettings() {

    // Given settings exist
    assertThat(systemSettingsRepository.count()).isEqualTo(1);

    // When updated
    SystemSettingsDto request = SystemSettingsDto.builder()
      .owner(existingSystemSettings.getOwner())
      .maxActionBatchSize(101)
      .recursionTimeoutMillis(3434)
      .build();
    SystemSettingsDto actual = systemSettingsDataService.save(request);

    // Then the record count is the same
    assertThat(systemSettingsRepository.count()).isEqualTo(1);
    assertThat(actual.getOwner()).isNotNull();

    // And the expected DTO is returned
    assertThat(actual).usingRecursiveComparison().isEqualTo(request);

  }
}
