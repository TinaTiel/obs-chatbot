package com.tinatiel.obschatbot.data.localuser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.data.error.DataPersistenceException;
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
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LocalUserServicesE2eIT {

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
    localUserRepository.deleteAll();
    localGroupRepository.deleteAll();

    // Verify initial counts
    assertThat(localGroupRepository.count()).isZero();
    assertThat(localUserRepository.count()).isZero();
  }

  @Test
  void createUser() {
    assertSaveUser(LocalUserDto.builder()
      .id(UUID.randomUUID())
      .owner(UUID.randomUUID())
      .platform(Platform.LOCAL)
      .username("mango")
      .build()
    );
    assertThat(localUserRepository.count()).isEqualTo(1);
    assertThat(localGroupRepository.count()).isEqualTo(0);

  }

  @Test
  void saveInvalidUser() {
    // no owner
    assertThatThrownBy(() -> {
      localUserService.save(LocalUserDto.builder()
        .id(UUID.randomUUID())
        .owner(null)
        .platform(Platform.LOCAL)
        .username("foo")
        .build()
      );
    }).isInstanceOf(IllegalArgumentException.class);

    // no platform
    assertThatThrownBy(() -> {
      localUserService.save(LocalUserDto.builder()
        .id(UUID.randomUUID())
        .owner(UUID.randomUUID())
        .platform(null)
        .username("foo")
        .build()
      );
    }).isInstanceOf(IllegalArgumentException.class);

    // no username
    assertThatThrownBy(() -> {
      localUserService.save(LocalUserDto.builder()
        .id(UUID.randomUUID())
        .owner(UUID.randomUUID())
        .platform(Platform.LOCAL)
        .username(null)
        .build()
      );
    }).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> {
      localUserService.save(LocalUserDto.builder()
        .id(UUID.randomUUID())
        .owner(UUID.randomUUID())
        .platform(Platform.LOCAL)
        .username("    ")
        .build()
      );
    }).isInstanceOf(IllegalArgumentException.class);

    assertThat(localUserRepository.count()).isZero();

  }

  @Test
  void manyBroadcastersNotAllowed() {

    // Given there is already a broadcaster
    UUID owner = UUID.randomUUID();
    Platform platform = Platform.TWITCH;
    LocalUserDto broadcaster = assertSaveUser(LocalUserDto.builder()
      .id(UUID.randomUUID())
      .owner(owner)
      .platform(platform)
      .username("broadcaster")
      .broadcaster(true)
      .build()
    );
    assertThat(localUserService.findBroadcasterForOwnerAndPlatform(
      broadcaster.getOwner(), broadcaster.getPlatform())).isPresent();

    // When another broadcaster is saved for the same platform and owner
    // Then an exception is thrown
    assertThatThrownBy(() -> {
      assertSaveUser(LocalUserDto.builder()
        .id(UUID.randomUUID())
        .owner(owner)
        .platform(platform)
        .username("another broadcaster")
        .broadcaster(true)
        .build()
      );
    }).isInstanceOf(DataPersistenceException.class);

    // And no extra user was created
    assertThat(localUserRepository.count()).isEqualTo(1);

  }

  @Test
  void duplicateUsernamesNotAllowed() {

    // Given there is already an user
    UUID owner = UUID.randomUUID();
    Platform platform = Platform.TWITCH;
    LocalUserDto user = assertSaveUser(LocalUserDto.builder()
      .id(UUID.randomUUID())
      .owner(owner)
      .platform(platform)
      .username("user")
      .broadcaster(true)
      .build()
    );
    assertThat(localUserService.findBroadcasterForOwnerAndPlatform(
      user.getOwner(), user.getPlatform())).isPresent();

    // When another user is saved for the same platform and username
    // Then an exception is thrown
    assertThatThrownBy(() -> {
      localUserService.save(LocalUserDto.builder()
        .id(UUID.randomUUID())
        .owner(owner)
        .platform(platform)
        .username(user.getUsername())
        .build()
      );
    }).isInstanceOf(DataPersistenceException.class);

    // And no extra user was created
    assertThat(localUserService.findByOwner(user.getOwner())).hasSize(1);
  }

  @Test
  void createUserGroupIgnored() {

    // Given a new user
    LocalUserDto request = LocalUserDto.builder()
      .id(UUID.randomUUID())
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
    assertThat(request).usingRecursiveComparison().ignoringFields("groups").isEqualTo(actual);

    // And the new groups were ignored
    assertThat(actual.getGroups()).isEmpty();
    assertThat(localGroupService.findByOwner(result.getOwner())).hasSize(0);

  }

  @Test
  void findAllByOwner() {

    // Given several users on the same owner
    UUID owner = UUID.randomUUID();
    LocalUserDto user1 = assertSaveUser(LocalUserDto.builder()
      .id(UUID.randomUUID())
      .owner(owner)
      .platform(Platform.TWITCH)
      .username("broadcaster")
      .broadcaster(true)
      .build()
    );
    LocalUserDto user2 = assertSaveUser(LocalUserDto.builder()
      .id(UUID.randomUUID())
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
      .id(UUID.randomUUID())
      .owner(owner)
      .platform(platform)
      .username("user1")
      .build()
    );
    assertSaveUser(LocalUserDto.builder()
      .id(UUID.randomUUID())
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
      .id(UUID.randomUUID())
      .owner(owner)
      .platform(platform)
      .username("broadcaster")
      .broadcaster(true)
      .build()
    );
    assertSaveUser(LocalUserDto.builder()
      .id(UUID.randomUUID())
      .owner(owner)
      .platform(platform)
      .username("follower")
      .broadcaster(false)
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
      .id(UUID.randomUUID())
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
        .owner(existing.getOwner())
        .platform(updateRequest.getPlatform())
        .username(updateRequest.getUsername())
        .broadcaster(updateRequest.isBroadcaster())
        .build()
      );

  }

  @Test
  void createGroup() {

    assertSaveGroup(LocalGroupDto.builder()
      .id(UUID.randomUUID())
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
      .id(UUID.randomUUID())
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
        .owner(existing.getOwner())
        .name(request.getName())
        .build()
      );

  }

  @Test
  void saveInvalidGroup() {

    // no owner
    assertThatThrownBy(() -> {
      localGroupService.save(LocalGroupDto.builder()
        .id(UUID.randomUUID())
        .owner(null)
        .name("foo")
        .build()
      );
    }).isInstanceOf(IllegalArgumentException.class);

    // no name
    assertThatThrownBy(() -> {
      localGroupService.save(LocalGroupDto.builder()
        .id(UUID.randomUUID())
        .owner(UUID.randomUUID())
        .name(null)
        .build()
      );
    }).isInstanceOf(IllegalArgumentException.class);

    // empty name
    assertThatThrownBy(() -> {
      localGroupService.save(LocalGroupDto.builder()
        .id(UUID.randomUUID())
        .owner(UUID.randomUUID())
        .name("   ")
        .build()
      );
    }).isInstanceOf(IllegalArgumentException.class);


    assertThat(localGroupRepository.count()).isZero();

  }

  @Test
  void duplicateGroupNameNotAllowed() {

    // Given an existing group
    LocalGroupDto existing =  assertSaveGroup(LocalGroupDto.builder()
      .id(UUID.randomUUID())
      .owner(UUID.randomUUID())
      .name("some group")
      .build()
    );
    assertThat(localGroupRepository.count()).isEqualTo(1);

    // When another group is saved with the same name
    // Then an exception is thrown
    assertThatThrownBy(() -> {
      localGroupService.save(
        LocalGroupDto.builder()
          .id(UUID.randomUUID())
          .owner(existing.getOwner())
          .name(existing.getName())
          .build()
      );
    }).isInstanceOf(DataPersistenceException.class);

  }

  @Test
  void invalidAssignmentRequest() {
    assertThatThrownBy(() -> {
      localUserAssignmentService.addAssignment(LocalUserGroupAssignmentDto.builder()
        .userId(null)
        .groupId(UUID.randomUUID())
        .build());
    }).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> {
      localUserAssignmentService.addAssignment(LocalUserGroupAssignmentDto.builder()
        .userId(UUID.randomUUID())
        .groupId(null)
        .build());
    }).isInstanceOf(IllegalArgumentException.class);

    assertThatThrownBy(() -> {
      localUserAssignmentService.removeAssignment(LocalUserGroupAssignmentDto.builder()
        .userId(null)
        .groupId(UUID.randomUUID())
        .build());
    }).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> {
      localUserAssignmentService.removeAssignment(LocalUserGroupAssignmentDto.builder()
        .userId(UUID.randomUUID())
        .groupId(null)
        .build());
    }).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void assignAndRetrieveUserWithGroups() {
    // Given an user and group exist
    LocalGroupDto existingGroup =  assertSaveGroup(LocalGroupDto.builder()
      .id(UUID.randomUUID())
      .owner(UUID.randomUUID())
      .name("some group")
      .build()
    );
    LocalUserDto existingUser = assertSaveUser(LocalUserDto.builder()
      .id(UUID.randomUUID())
      .owner(UUID.randomUUID())
      .platform(Platform.LOCAL)
      .username("existing user")
      .build()
    );

    // When assigned
    localUserAssignmentService.addAssignment(LocalUserGroupAssignmentDto.builder()
      .userId(existingUser.getId())
      .groupId(existingGroup.getId())
      .build());

    assertThat(localUserRepository.count()).isEqualTo(1);
    assertThat(localGroupRepository.count()).isEqualTo(1);

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
    // Given an user and group exist
    LocalGroupDto existingGroup =  assertSaveGroup(LocalGroupDto.builder()
      .id(UUID.randomUUID())
      .owner(UUID.randomUUID())
      .name("some group")
      .build()
    );
    LocalUserDto existingUser = assertSaveUser(LocalUserDto.builder()
      .id(UUID.randomUUID())
      .owner(UUID.randomUUID())
      .platform(Platform.LOCAL)
      .username("existing user")
      .build()
    );

    // When assigned
    localUserAssignmentService.addAssignment(LocalUserGroupAssignmentDto.builder()
      .userId(existingUser.getId())
      .groupId(existingGroup.getId())
      .build());
    assertThat(localUserRepository.count()).isEqualTo(1);
    assertThat(localGroupRepository.count()).isEqualTo(1);

    // Then the retrieved user has the group assigned to it
    assertThat(localUserService.findById(existingUser.getId()).get().getGroups()).hasSize(1);

    // When unassigned
    localUserAssignmentService.removeAssignment(LocalUserGroupAssignmentDto.builder()
      .userId(existingUser.getId())
      .groupId(existingGroup.getId())
      .build());
    assertThat(localUserRepository.count()).isEqualTo(1);
    assertThat(localGroupRepository.count()).isEqualTo(1);

    // Then the retrieved user has the group remove from it
    assertThat(localUserService.findById(existingUser.getId()).get().getGroups()).isEmpty();

  }

  @Test
  void assignAndDeleteGroup() {

    // Given an user and group exist
    LocalGroupDto existingGroup =  assertSaveGroup(LocalGroupDto.builder()
      .id(UUID.randomUUID())
      .owner(UUID.randomUUID())
      .name("some group")
      .build()
    );
    LocalUserDto existingUser = assertSaveUser(LocalUserDto.builder()
      .id(UUID.randomUUID())
      .owner(UUID.randomUUID())
      .platform(Platform.LOCAL)
      .username("existing user")
      .build()
    );

    // And have been assigned to each other
    localUserAssignmentService.addAssignment(LocalUserGroupAssignmentDto.builder()
      .userId(existingUser.getId())
      .groupId(existingGroup.getId())
      .build());
    assertThat(localUserRepository.count()).isEqualTo(1);
    assertThat(localGroupRepository.count()).isEqualTo(1);
    assertThat(localUserService.findById(existingUser.getId()).get().getGroups()).hasSize(1);

    // When the group is deleted
    localGroupService.delete(existingGroup.getId());

    // Then it is deleted
    assertThat(localGroupService.findById(existingGroup.getId())).isEmpty();
    assertThat(localGroupRepository.count()).isZero();

    // Then the user exists, but has no assignment
    assertThat(localUserService.findById(existingUser.getId()).get().getGroups()).isEmpty();

  }

  @Test
  void assignAndDeleteUser() {

    // Given an user and group exist
    LocalGroupDto existingGroup =  assertSaveGroup(LocalGroupDto.builder()
      .id(UUID.randomUUID())
      .owner(UUID.randomUUID())
      .name("some group")
      .build()
    );
    LocalUserDto existingUser = assertSaveUser(LocalUserDto.builder()
      .id(UUID.randomUUID())
      .owner(UUID.randomUUID())
      .platform(Platform.LOCAL)
      .username("existing user")
      .build()
    );

    // And have been assigned to each other
    localUserAssignmentService.addAssignment(LocalUserGroupAssignmentDto.builder()
      .userId(existingUser.getId())
      .groupId(existingGroup.getId())
      .build());
    assertThat(localUserRepository.count()).isEqualTo(1);
    assertThat(localGroupRepository.count()).isEqualTo(1);
    assertThat(localUserService.findById(existingUser.getId()).get().getGroups()).hasSize(1);

    // When the user is deleted
    localUserService.delete(existingUser.getId());

    // Then it is deleted
    assertThat(localUserService.findById(existingUser.getId())).isEmpty();
    assertThat(localUserRepository.count()).isZero();

    // Then the group still exists
    assertThat(localGroupService.findById(existingGroup.getId())).isNotEmpty();

  }

  private LocalGroupDto assertSaveGroup(LocalGroupDto request) {

    // When saved
    LocalGroupDto result = localGroupService.save(request);

    // Then it can be retrieved
    LocalGroupDto actual = localGroupService.findById(result.getId()).get();
    assertThat(actual).usingRecursiveComparison().isEqualTo(result);
    assertThat(request).usingRecursiveComparison().isEqualTo(actual);
    List<LocalGroupDto> actuals = localGroupService.findByOwner(result.getOwner());
    assertThat(actuals).usingRecursiveComparison().isEqualTo(Arrays.asList(
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
    assertThat(request).usingRecursiveComparison().isEqualTo(actual);

    return result;
  }

}
