package com.tinatiel.obschatbot.data.localuser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;

import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.data.common.CommonConfig;
import com.tinatiel.obschatbot.data.localuser.entity.LocalGroupRepository;
import com.tinatiel.obschatbot.data.localuser.entity.LocalUserRepository;
import com.tinatiel.obschatbot.data.localuser.model.LocalGroupDto;
import com.tinatiel.obschatbot.data.localuser.model.LocalUserDto;
import com.tinatiel.obschatbot.data.localuser.model.LocalUserGroupAssignmentDto;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
  LocalUserServicesIT.TestConfig.class})
@DataJpaTest
public class LocalUserServicesIT {

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

  @Autowired
  LocalUserAssignmentService localUserAssignmentService;

  @BeforeEach
  void setUp() {
    localGroupRepository.deleteAll();
    localUserRepository.deleteAll();

    // Verify initial counts
    assertThat(localGroupRepository.count()).isZero();
    assertThat(localUserRepository.count()).isZero();
  }

  @Test
  void createUser() {
    assertSaveUser(LocalUserDto.builder()
      .owner(UUID.randomUUID())
      .platform(Platform.LOCAL)
      .username("mango")
      .build()
    );
    assertThat(localUserRepository.count()).isEqualTo(1);
    assertThat(localGroupRepository.count()).isEqualTo(0);

  }

  @Test
  void saveWithoutRequiredValues() {
    // no owner
    assertThatThrownBy(() -> {
      localUserService.save(LocalUserDto.builder()
        .owner(null)
        .platform(Platform.LOCAL)
        .username("foo")
        .build()
      );
    }).isInstanceOf(IllegalArgumentException.class);

    // no platform
    assertThatThrownBy(() -> {
      localUserService.save(LocalUserDto.builder()
        .owner(UUID.randomUUID())
        .platform(null)
        .username("foo")
        .build()
      );
    }).isInstanceOf(IllegalArgumentException.class);

    // no username
    assertThatThrownBy(() -> {
      localUserService.save(LocalUserDto.builder()
        .owner(UUID.randomUUID())
        .platform(Platform.LOCAL)
        .username(null)
        .build()
      );
    }).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> {
      localUserService.save(LocalUserDto.builder()
        .owner(UUID.randomUUID())
        .platform(Platform.LOCAL)
        .username("    ")
        .build()
      );
    }).isInstanceOf(IllegalArgumentException.class);

    assertThat(localUserRepository.count()).isZero();

  }

  @Test
  void createUserGroupIgnored() {

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
  void findAllByOwner() {

    // Given several users on the same owner
    UUID owner = UUID.randomUUID();
    LocalUserDto user1 = assertSaveUser(LocalUserDto.builder()
      .owner(owner)
      .platform(Platform.TWITCH)
      .username("broadcaster")
      .broadcaster(true)
      .build()
    );
    LocalUserDto user2 = assertSaveUser(LocalUserDto.builder()
      .owner(owner)
      .platform(Platform.LOCAL)
      .username("follower")
      .build()
    );
    assertThat(localUserRepository.count()).isEqualTo(2);

    // When we try to find by owner
    List<LocalUserDto> result = localUserService.findByOwner(owner);

    // Then only the broadcaster is returned
    assertThat(result).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(Arrays.asList(
      user1, user2
    ));
  }

  @Test
  void findUniqueUser() {
    // Given different users under the same owner and platform
    UUID owner = UUID.randomUUID();
    Platform platform = Platform.TWITCH;
    LocalUserDto user = assertSaveUser(LocalUserDto.builder()
      .owner(owner)
      .platform(platform)
      .username("user1")
      .build()
    );
    assertSaveUser(LocalUserDto.builder()
      .owner(owner)
      .platform(platform)
      .username("user2")
      .build()
    );
    assertThat(localUserRepository.count()).isEqualTo(2);

    // When searched
    Optional<LocalUserDto> result = localUserService.findByOwnerAndPlatformAndUsername(
      owner, platform, user.getUsername()
    );

    // Then a unique user an be found
    assertThat(result).isPresent().get().usingRecursiveComparison().isEqualTo(user);

  }

  @Test
  void findBroadcaster() {

    // Given a broadcaster and non-broadcaster with the same owner and platform
    UUID owner = UUID.randomUUID();
    Platform platform = Platform.TWITCH;
    LocalUserDto broadcaster = assertSaveUser(LocalUserDto.builder()
      .owner(owner)
      .platform(platform)
      .username("broadcaster")
      .broadcaster(true)
      .build()
    );
    assertSaveUser(LocalUserDto.builder()
      .owner(owner)
      .platform(platform)
      .username("follower")
      .build()
    );
    assertThat(localUserRepository.count()).isEqualTo(2);

    // When we try to find the broadcaster for the platform
    Optional<LocalUserDto> result = localUserService.findBroadcasterForOwnerAndPlatform(owner, platform);

    // Then only the broadcaster is returned
    assertThat(result).isPresent().get().usingRecursiveComparison().isEqualTo(broadcaster);

  }

  @Test
  void updateUser() {

    // Given an existing user
    LocalUserDto existing = assertSaveUser(LocalUserDto.builder()
      .owner(UUID.randomUUID())
      .platform(Platform.LOCAL)
      .username("existing")
      .build()
    );

    // When updated
    LocalUserDto updateRequest = LocalUserDto.builder()
      .id(existing.getId())
      .owner(UUID.randomUUID())
      .platform(Platform.TWITCH)
      .username("updated existing")
      .broadcaster(true)
      .build();
    localUserService.save(updateRequest);

    // Then no duplicates are created
    assertThat(localUserRepository.count()).isEqualTo(1);

    // Then it is updated as expected everywhere id
    assertThat(localUserService.findById(existing.getId())).get()
      .usingRecursiveComparison()
      .isEqualTo(LocalUserDto.builder()
        .id(existing.getId())
        .owner(updateRequest.getOwner())
        .platform(updateRequest.getPlatform())
        .username(updateRequest.getUsername())
        .broadcaster(updateRequest.isBroadcaster())
        .build()
      );

  }

  @Disabled
  @Test
  void manyBroadcasters() {
    fail("to do; allow many broadcasters?");
  }

  @Test
  void createGroup() {

    assertSaveGroup(LocalGroupDto.builder()
      .owner(UUID.randomUUID())
      .name("some group")
      .build()
    );
    assertThat(localGroupRepository.count()).isEqualTo(1);

  }

  @Test
  void updateGroup() {

    // Given an existing group
    LocalGroupDto existing =  assertSaveGroup(LocalGroupDto.builder()
      .owner(UUID.randomUUID())
      .name("some group")
      .build()
    );
    assertThat(localGroupRepository.count()).isEqualTo(1);

    // When updated
    LocalGroupDto request = LocalGroupDto.builder()
      .id(existing.getId())
      .owner(UUID.randomUUID())
      .name("some updated group")
      .build();
    localGroupService.save(request);

    // Then there are no duplicates
    assertThat(localGroupRepository.count()).isEqualTo(1);

    // Then it is updated as expected
    assertThat(localGroupService.findById(existing.getId()))
      .isPresent()
      .get()
      .usingRecursiveComparison()
      .isEqualTo(LocalGroupDto.builder()
        .id(existing.getId())
        .owner(request.getOwner())
        .name(request.getName())
        .build()
      );

  }

  @Test
  void saveInvalidGroup() {

    // no owner
    assertThatThrownBy(() -> {
      localGroupService.save(LocalGroupDto.builder()
        .owner(null)
        .name("foo")
        .build()
      );
    }).isInstanceOf(IllegalArgumentException.class);

    // no name
    assertThatThrownBy(() -> {
      localGroupService.save(LocalGroupDto.builder()
        .owner(UUID.randomUUID())
        .name(null)
        .build()
      );
    }).isInstanceOf(IllegalArgumentException.class);

    // empty name
    assertThatThrownBy(() -> {
      localGroupService.save(LocalGroupDto.builder()
        .owner(UUID.randomUUID())
        .name("   ")
        .build()
      );
    }).isInstanceOf(IllegalArgumentException.class);


    assertThat(localGroupRepository.count()).isZero();

  }

  @Test
  void invalidAssignmentRequest() {
    assertThatThrownBy(() -> {
      localUserAssignmentService.addAssignment(LocalUserGroupAssignmentDto.builder()
        .localUserId(null)
        .localGroupId(UUID.randomUUID())
        .build());
    }).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> {
      localUserAssignmentService.addAssignment(LocalUserGroupAssignmentDto.builder()
        .localUserId(UUID.randomUUID())
        .localGroupId(null)
        .build());
    }).isInstanceOf(IllegalArgumentException.class);

    assertThatThrownBy(() -> {
      localUserAssignmentService.removeAssignment(LocalUserGroupAssignmentDto.builder()
        .localUserId(null)
        .localGroupId(UUID.randomUUID())
        .build());
    }).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> {
      localUserAssignmentService.removeAssignment(LocalUserGroupAssignmentDto.builder()
        .localUserId(UUID.randomUUID())
        .localGroupId(null)
        .build());
    }).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void assignAndRetrieveUserWithGroups() {
    // Given an user and group exist
    LocalGroupDto existingGroup =  assertSaveGroup(LocalGroupDto.builder()
      .owner(UUID.randomUUID())
      .name("some group")
      .build()
    );
    LocalUserDto existingUser = assertSaveUser(LocalUserDto.builder()
      .owner(UUID.randomUUID())
      .platform(Platform.LOCAL)
      .username("existing user")
      .build()
    );

    // When assigned
    localUserAssignmentService.addAssignment(LocalUserGroupAssignmentDto.builder()
      .localUserId(existingUser.getId())
      .localGroupId(existingGroup.getId())
      .build());

    // Then the retrieved user has the group assigned to it
    assertThat(localUserService.findById(existingUser.getId()))
      .isPresent()
      .get()
      .usingRecursiveComparison()
      .isEqualTo(LocalUserDto.builder()
        .id(existingUser.getId())
        .owner(existingUser.getOwner())
        .platform(existingUser.getPlatform())
        .username(existingUser.getUsername())
        .broadcaster(existingUser.isBroadcaster())
        .groups(Arrays.asList(existingGroup))
        .build()
      );

  }

  @Test
  void assignAndRemoveUsersToFromGroup() {
    fail("to do");
  }

  @Test
  void assignAndDeleteGroup() {
    fail("to do");
  }

  @Test
  void assignAndDeleteUser() {
    fail("to do");
  }

  private LocalGroupDto assertSaveGroup(LocalGroupDto request) {

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

    return result;
  }

  private LocalUserDto assertSaveUser(LocalUserDto request) {
    // When saved
    LocalUserDto result = localUserService.save(request);

    // Then it can be retrieved
    LocalUserDto actual = localUserService.findById(result.getId()).get();
    assertThat(actual).usingRecursiveComparison().isEqualTo(result);
    assertThat(request).usingRecursiveComparison().ignoringFields("id").isEqualTo(actual);

    return result;
  }

}
