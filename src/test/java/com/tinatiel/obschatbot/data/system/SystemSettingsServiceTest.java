package com.tinatiel.obschatbot.data.system;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;

import com.tinatiel.obschatbot.data.common.CommonConfig;
import com.tinatiel.obschatbot.data.error.DataPersistenceException;
import com.tinatiel.obschatbot.data.owner.SystemOwnerService;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {CommonConfig.class, SystemDataConfig.class, SystemSettingsServiceTest.TestConfig.class})
@DataJpaTest
public class SystemSettingsServiceTest {

  @EnableJpaRepositories(basePackages = "com.tinatiel.obschatbot.data.system.entity")
  @Configuration
  static class TestConfig {

  }

  @Autowired
  SystemSettingsRepository systemSettingsRepository;

  @Autowired
  SystemSettingsService systemSettingsService;

  SystemSettingsEntity existingSystemSettings;

  @BeforeEach
  void setUp() {
    systemSettingsRepository.deleteAll();
    SystemSettingsEntity existing = new SystemSettingsEntity();
    existing.setOwner(UUID.randomUUID());
    existing.setRecursionTimeoutMillis(42);
    existing.setMaxActionBatchSize(69);
    existingSystemSettings = systemSettingsRepository.saveAndFlush(existing);
    assertThat(existingSystemSettings.getId()).isNotNull();
  }

  @Test
  void getExistingSettings() {

    // Given settings exist
    assertThat(systemSettingsRepository.count()).isEqualTo(1);

    // When called
    Optional<SystemSettingsDto> actual = systemSettingsService.findByOwner(existingSystemSettings.getOwner());

    // Then the expected DTO is returned
    assertThat(actual).isPresent();
    SystemSettingsDto expected = SystemSettingsDto.builder()
      .owner(existingSystemSettings.getOwner())
      .id(existingSystemSettings.getId())
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
    SystemSettingsDto result = systemSettingsService.save(request);

    // Then record count has increased
    assertThat(systemSettingsRepository.count()).isEqualTo(2);

    // And it matches as expected
    SystemSettingsDto retrieved = systemSettingsService.findByOwner(request.getOwner()).get();
    assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(request);
    assertThat(result).usingRecursiveComparison().isEqualTo(retrieved);

  }

  @Test
  void nullOwner() {
    assertThatThrownBy(() -> {
      systemSettingsService.save(SystemSettingsDto.builder()
        .build());
    }).isInstanceOf(DataPersistenceException.class);
  }

  @Test
  void ownerOnlyAllowedOneSettingsSet() {

    assertThatThrownBy(() -> {
      systemSettingsService.save(SystemSettingsDto.builder()
        .owner(existingSystemSettings.getOwner())
        .build());
    }).isInstanceOf(DataPersistenceException.class);

  }

  @Test
  void updateExistingSettings() {

    // Given settings exist
    assertThat(systemSettingsRepository.count()).isEqualTo(1);

    // When updated
    SystemSettingsDto request = SystemSettingsDto.builder()
      .owner(existingSystemSettings.getOwner())
      .id(existingSystemSettings.getId())
      .maxActionBatchSize(101)
      .recursionTimeoutMillis(3434)
      .build();
    SystemSettingsDto actual = systemSettingsService.save(request);

    // Then the record count is the same
    assertThat(systemSettingsRepository.count()).isEqualTo(1);
    assertThat(actual.getOwner()).isNotNull();

    // And the expected DTO is returned
    assertThat(actual).usingRecursiveComparison().isEqualTo(request);

  }
}
