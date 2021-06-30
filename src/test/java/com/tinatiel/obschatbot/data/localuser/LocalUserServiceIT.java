package com.tinatiel.obschatbot.data.localuser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.data.common.CommonConfig;
import com.tinatiel.obschatbot.data.localuser.entity.LocalGroupEntity;
import com.tinatiel.obschatbot.data.localuser.entity.LocalGroupRepository;
import com.tinatiel.obschatbot.data.localuser.entity.LocalUserEntity;
import com.tinatiel.obschatbot.data.localuser.entity.LocalUserRepository;
import com.tinatiel.obschatbot.data.localuser.model.LocalGroupDto;
import com.tinatiel.obschatbot.data.localuser.model.LocalUserDto;
import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
  CommonConfig.class,
  LocalUserConfig.class,
  LocalUserServiceIT.TestConfig.class})
@DataJpaTest
public class LocalUserServiceIT {

  @EnableJpaRepositories(basePackages = "com.tinatiel.obschatbot.data.localuser")
  @Configuration
  static class TestConfig {}

  @Autowired
  LocalUserRepository localUserRepository;

  @Autowired
  LocalGroupRepository localGroupRepository;

  @Autowired
  LocalUserService localUserService;

  @Autowired
  LocalGroupService localGroupService;

  UUID existingOwner = UUID.randomUUID();
  LocalUserEntity existingLocalUserEntity;
  LocalGroupEntity existingGroupEntity;

  @BeforeEach
  void setUp() {
    localGroupRepository.deleteAll();
    localUserRepository.deleteAll();

    // Create an existing user
    LocalUserEntity vExistingUser = new LocalUserEntity();
    vExistingUser.setOwner(existingOwner);
    vExistingUser.setPlatform(Platform.LOCAL);
    vExistingUser.setUsername("existing user");
    vExistingUser.setBroadcaster(true);
    existingLocalUserEntity = localUserRepository.save(vExistingUser);

    // Create an existing group
    LocalGroupEntity vExistingGroup = new LocalGroupEntity();
    vExistingGroup.setOwner(existingOwner);
    vExistingGroup.setName("existing group");
    existingGroupEntity = localGroupRepository.save(vExistingGroup);

    // Verify initial counts
    assertThat(localGroupRepository.count()).isEqualTo(1);
    assertThat(localUserRepository.count()).isEqualTo(1);
  }

  @Test
  void createNewUser() {

    // Given a new user
    LocalUserDto request = LocalUserDto.builder()
      .owner(UUID.randomUUID())
      .platform(Platform.LOCAL)
      .username("mango")
      .build();

    // When saved
    LocalUserDto result = localUserService.save(request);

    // Then it can be retrieved
    LocalUserDto actual = localUserService.findById(result.getId()).get();
    assertThat(actual).usingRecursiveComparison().isEqualTo(result);
    assertThat(request).usingRecursiveComparison().ignoringFields("id").isEqualTo(actual);

  }

  @Test
  void createNewUserWithGroupIgnored() {

    // Given a new user
    LocalUserDto request = LocalUserDto.builder()
      .owner(UUID.randomUUID())
      .platform(Platform.LOCAL)
      .username("mango")
      .groups(Arrays.asList(
        LocalGroupDto.builder().owner(UUID.randomUUID()).name("group1").build(),
        LocalGroupDto.builder().owner(UUID.randomUUID()).name("group2").build(),
        LocalGroupDto.builder().owner(UUID.randomUUID()).name("group3").build()
      ))
      .build();

    // When saved
    LocalUserDto result = localUserService.save(request);

    // Then it can be retrieved
    LocalUserDto actual = localUserService.findById(result.getId()).get();
    assertThat(actual).usingRecursiveComparison().isEqualTo(result);
    assertThat(request).usingRecursiveComparison().ignoringFields("id", "groups").isEqualTo(actual);

    // And the new groups were ignored
    assertThat(actual.getGroups()).isEmpty();
    assertThat(localGroupService.findByOwner(result.getOwner())).hasSize(0);

  }

  @Test
  void updateExistingUser() {

    // Given an update to an existing user
    LocalUserDto request = LocalUserDto.builder()
      .id(existingLocalUserEntity.getId())
      .owner(existingLocalUserEntity.getOwner())
      .platform(Platform.TWITCH)
      .username("mango")
      .broadcaster(true)
      .build();

    // When saved
    LocalUserDto result = localUserService.save(request);

    // Then it matches as expected
    LocalUserDto actual = localUserService.findById(result.getId()).get();
    assertThat(actual).usingRecursiveComparison().isEqualTo(result);
    assertThat(request).usingRecursiveComparison().isEqualTo(actual);

  }

  @Test
  void findByOwnerAndPlatformAndUsername() {
    assertThat(localUserService.findByOwnerAndPlatformAndUsername(
      existingLocalUserEntity.getOwner(),
      existingLocalUserEntity.getPlatform(),
      existingLocalUserEntity.getUsername())
    ).isPresent().get().usingRecursiveComparison().isEqualTo(LocalUserDto.builder()
      .id(existingLocalUserEntity.getId())
      .owner(existingLocalUserEntity.getOwner())
      .platform(existingLocalUserEntity.getPlatform())
      .username(existingLocalUserEntity.getUsername())
      .broadcaster(existingLocalUserEntity.isBroadcaster())
      .build()
    );
  }

  @Test
  void findBroadcasterForOwnerAndPlatform() {
    assertThat(existingLocalUserEntity.isBroadcaster()).isTrue();
    assertThat(localUserService.findBroadcasterForOwnerAndPlatform(
      existingLocalUserEntity.getOwner(), existingLocalUserEntity.getPlatform()
    )).isPresent().get().usingRecursiveComparison().isEqualTo(LocalUserDto.builder()
      .id(existingLocalUserEntity.getId())
      .owner(existingLocalUserEntity.getOwner())
      .platform(existingLocalUserEntity.getPlatform())
      .username(existingLocalUserEntity.getUsername())
      .broadcaster(existingLocalUserEntity.isBroadcaster())
      .build()
    );
  }

  @Disabled
  @Test
  void retrieveExistingUserWithGroups() {
    fail("to do");
  }
}
