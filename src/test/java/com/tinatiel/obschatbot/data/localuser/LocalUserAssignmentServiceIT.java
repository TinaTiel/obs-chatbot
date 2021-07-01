package com.tinatiel.obschatbot.data.localuser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.data.common.CommonConfig;
import com.tinatiel.obschatbot.data.localuser.entity.LocalGroupEntity;
import com.tinatiel.obschatbot.data.localuser.entity.LocalGroupRepository;
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
  LocalUserAssignmentServiceIT.TestConfig.class})
@DataJpaTest
public class LocalUserAssignmentServiceIT {

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
  LocalGroupEntity existingGroupEntity;
  LocalGroupDto existingGroupDto;

  @BeforeEach
  void setUp() {
    localGroupRepository.deleteAll();
    localUserRepository.deleteAll();
    LocalGroupEntity vExistingGroup = new LocalGroupEntity();
    vExistingGroup.setOwner(existingOwner);
    vExistingGroup.setName("existing group");
    existingGroupEntity = localGroupRepository.save(vExistingGroup);
    existingGroupDto = LocalGroupDto.builder()
      .id(existingGroupEntity.getId())
      .owner(existingGroupEntity.getOwner())
      .name(existingGroupEntity.getName())
      .build();
    assertThat(localGroupRepository.count()).isEqualTo(1);
    assertThat(localUserRepository.count()).isEqualTo(0);
  }


//  @Test
//  void assignToGroup() {
//
//
//    // Given a new user member of one group
//    LocalUserDto.LocalUserDtoBuilder builder = LocalUserDto.builder()
//      .owner(existingOwner)
//      .platform(Platform.LOCAL)
//      .username("mango")
//      .groups(Arrays.asList(
//        LocalGroupDto.builder().owner(existingOwner).name("group1").build()
//      ));
//
//    // When saved
//    LocalUserDto request1 = builder.build();
//    LocalUserDto result1 = localUserService.save(request1);
//
//    // Then it can be retrieved
//    LocalUserDto actual1 = localUserService.findById(result1.getId()).get();
//    assertThat(actual1).usingRecursiveComparison().isEqualTo(result1);
//    assertThat(request1).usingRecursiveComparison().ignoringFields("id").isEqualTo(actual1);
//
//    // And then when we update it, adding new group memberships (some existing, some new)
//    builder.groups(Arrays.asList(
//      existingGroupDto,
//      LocalGroupDto.builder().owner(existingOwner).name("group1").build(),
//      LocalGroupDto.builder().owner(existingOwner).name("group2").build()
//    ));
//
//    // When saved
//    LocalUserDto request2 = builder.build();
//    LocalUserDto result2 = localUserService.save(request2);
//
//    // Then it can be retrieved
//    LocalUserDto actual2 = localUserService.findById(result2.getId()).get();
//    assertThat(actual2).usingRecursiveComparison().isEqualTo(result2);
//    assertThat(request2).usingRecursiveComparison().ignoringFields("id", "groups.id").isEqualTo(actual2);
//
//  }
//
//  @Test
//  void removeFromGroup() {
//    // Given a new user member of one group
//    LocalUserDto.LocalUserDtoBuilder builder = LocalUserDto.builder()
//      .owner(existingOwner)
//      .platform(Platform.LOCAL)
//      .username("mango")
//      .groups(Arrays.asList(
//        LocalGroupDto.builder().owner(existingOwner).name("group1").build(),
//        LocalGroupDto.builder().owner(existingOwner).name("group2").build(),
//        LocalGroupDto.builder().owner(existingOwner).name("group3").build()
//      ));
//
//    // When saved
//    LocalUserDto request1 = builder.build();
//    LocalUserDto result1 = localUserService.save(request1);
//
//    // Then it can be retrieved
//    LocalUserDto actual1 = localUserService.findById(result1.getId()).get();
//    assertThat(actual1).usingRecursiveComparison().isEqualTo(result1);
//    assertThat(request1).usingRecursiveComparison().ignoringFields("id").isEqualTo(actual1);
//
//    // And then when we update it, removing group memberships
//    builder.groups(Arrays.asList(
//      LocalGroupDto.builder().owner(existingOwner).name("group2").build()
//    ));
//
//    // When saved
//    LocalUserDto request2 = builder.build();
//    LocalUserDto result2 = localUserService.save(request2);
//
//    // Then it can be retrieved
//    LocalUserDto actual2 = localUserService.findById(result2.getId()).get();
//    assertThat(actual2).usingRecursiveComparison().isEqualTo(result2);
//    assertThat(request2).usingRecursiveComparison().ignoringFields("id", "groups.id").isEqualTo(actual2);
//
//    // And the other groups still exist
//
//  }



}
