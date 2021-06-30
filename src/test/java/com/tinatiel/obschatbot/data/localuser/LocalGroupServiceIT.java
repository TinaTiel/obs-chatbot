package com.tinatiel.obschatbot.data.localuser;

import static org.assertj.core.api.Assertions.assertThat;

import com.tinatiel.obschatbot.data.common.CommonConfig;
import com.tinatiel.obschatbot.data.localuser.entity.LocalGroupEntity;
import com.tinatiel.obschatbot.data.localuser.entity.LocalGroupRepository;
import com.tinatiel.obschatbot.data.localuser.entity.LocalUserRepository;
import com.tinatiel.obschatbot.data.localuser.model.LocalGroupDto;
import java.util.Arrays;
import java.util.List;
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
  LocalUserConfig.class,
  LocalGroupServiceIT.TestConfig.class})
@DataJpaTest
public class LocalGroupServiceIT {

  @EnableJpaRepositories(basePackages = "com.tinatiel.obschatbot.data.localuser")
  @Configuration
  static class TestConfig {}

  @Autowired
  LocalUserRepository localUserRepository;

  @Autowired
  LocalGroupRepository localGroupRepository;

  @Autowired
  LocalGroupService localGroupService;

  UUID existingOwner = UUID.randomUUID();
  LocalGroupEntity existingGroupEntity;

  @BeforeEach
  void setUp() {
    localGroupRepository.deleteAll();
    localUserRepository.deleteAll();
    LocalGroupEntity vExistingGroup = new LocalGroupEntity();
    vExistingGroup.setOwner(existingOwner);
    vExistingGroup.setName("existing group");
    existingGroupEntity = localGroupRepository.save(vExistingGroup);
    assertThat(localGroupRepository.count()).isEqualTo(1);
    assertThat(localUserRepository.count()).isEqualTo(0);
  }

  @Test
  void createGroup() {

    // Given a new group
    LocalGroupDto request = LocalGroupDto.builder()
      .owner(UUID.randomUUID())
      .name("some group")
      .build();

    // When saved
    LocalGroupDto result = localGroupService.save(request);

    // Then it can be retrieved
    LocalGroupDto actual = localGroupService.findById(result.getId()).get();
    assertThat(actual).usingRecursiveComparison().isEqualTo(result);
    assertThat(request).usingRecursiveComparison().ignoringFields("id").isEqualTo(actual);
    List<LocalGroupDto> actuals = localGroupService.findByOwner(result.getOwner());
    assertThat(actuals).usingRecursiveComparison().ignoringFields("id").isEqualTo(Arrays.asList(
      request
    ));

  }

  @Test
  void updateGroup() {

    // Given a request to update an existing group
    LocalGroupDto request = LocalGroupDto.builder()
      .id(existingGroupEntity.getId())
      .owner(existingGroupEntity.getOwner())
      .name("some other group")
      .build();

    // When saved
    LocalGroupDto result = localGroupService.save(request);

    // Then it can be retrieved
    LocalGroupDto actual = localGroupService.findById(result.getId()).get();
    assertThat(actual).usingRecursiveComparison().isEqualTo(result);
    assertThat(request).usingRecursiveComparison().isEqualTo(actual);

    // And the group is updated
    List<LocalGroupDto> actuals = localGroupService.findByOwner(request.getOwner());
    assertThat(actuals).usingRecursiveComparison().ignoringFields("id").isEqualTo(Arrays.asList(
      request
    ));

  }

}
