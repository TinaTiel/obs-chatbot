package com.tinatiel.obschatbot.data.localuser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.data.common.CommonConfig;
import com.tinatiel.obschatbot.data.localuser.entity.LocalGroupRepository;
import com.tinatiel.obschatbot.data.localuser.entity.LocalUserRepository;
import com.tinatiel.obschatbot.data.localuser.model.LocalUserDto;
import com.tinatiel.obschatbot.data.system.SystemDataConfig;
import com.tinatiel.obschatbot.data.system.SystemSettingsDataServiceTest;
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
  LocalUserServiceTest.TestConfig.class})
@DataJpaTest
public class LocalUserServiceTest {

  @EnableJpaRepositories(basePackages = "com.tinatiel.obschatbot.data.localuser")
  @Configuration
  static class TestConfig {}

  @Autowired
  LocalUserRepository localUserRepository;

  @Autowired
  LocalGroupRepository localGroupRepository;

  @Autowired
  LocalUserService localUserService;

  @BeforeEach
  void setUp() {
    localGroupRepository.deleteAll();
    localUserRepository.deleteAll();
  }

  @Test
  void createNewUser() {

    // Given a new user
    LocalUserDto request = LocalUserDto.builder()
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
  void createNewUserWithGroup() {
    fail("todo");
  }

  @Test
  void updateExistingUser() {
    fail("todo");
  }

  @Test
  void addToGroup() {
    fail("todo");
  }

  @Test
  void removeFromGroup() {
    fail("todo");
  }

  @Test
  void deleteUserDoesNotDeleteGroup() {
    fail("todo");
  }

  @Test
  void findByOwnerAndPlatformAndUsername() {
    fail("todo");
  }

  @Test
  void findBroadcasterForOwnerAndPlatform() {
    fail("todo");
  }

}
