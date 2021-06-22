package com.tinatiel.obschatbot.data.localuser;

import static org.assertj.core.api.Assertions.assertThat;

import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.data.localuser.entity.LocalGroupEntity;
import com.tinatiel.obschatbot.data.localuser.entity.LocalUserEntity;
import com.tinatiel.obschatbot.data.localuser.mapper.LocalUserMapper;
import com.tinatiel.obschatbot.data.localuser.model.LocalGroupDto;
import com.tinatiel.obschatbot.data.localuser.model.LocalUserDto;
import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class LocalUserMapperTest {

  LocalUserMapper mapper = Mappers.getMapper(LocalUserMapper.class);

  @Test
  void mapLocalUser() {

    // Given users
    UUID owner = UUID.randomUUID();
    LocalUserDto expectedDto = LocalUserDto.builder()
      .owner(owner)
      .platform(Platform.LOCAL)
      .username("mr_roboto")
      .broadcaster(true)
      .build();

    LocalUserEntity expectedEntity = new LocalUserEntity();
    expectedEntity.setOwner(owner);
    expectedEntity.setPlatform(Platform.LOCAL);
    expectedEntity.setUsername("mr_roboto");
    expectedEntity.setBroadcaster(true);

    // When mapped
    LocalUserEntity actualEntity = mapper.map(expectedDto);
    LocalUserDto actualDto = mapper.map(expectedEntity);

    // Then they match
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
    assertThat(actualEntity).usingRecursiveComparison().isEqualTo(expectedEntity);

  }

  @Test
  void mapLocalUserWithGroups() {

    // Given users with groups
    UUID owner = UUID.randomUUID();
    LocalUserDto expectedDto = LocalUserDto.builder()
      .groups(Arrays.asList(
        LocalGroupDto.builder().owner(owner).name("group1").build(),
        LocalGroupDto.builder().owner(owner).name("group2").build(),
        LocalGroupDto.builder().owner(owner).name("group3").build()
      ))
      .build();

    LocalGroupEntity groupEntity1 = new LocalGroupEntity();
    groupEntity1.setOwner(owner);
    groupEntity1.setName("group1");
    LocalGroupEntity groupEntity2 = new LocalGroupEntity();
    groupEntity2.setOwner(owner);
    groupEntity2.setName("group2");
    LocalGroupEntity groupEntity3 = new LocalGroupEntity();
    groupEntity3.setOwner(owner);
    groupEntity3.setName("group3");
    LocalUserEntity expectedEntity = new LocalUserEntity();
    expectedEntity.setGroups(Arrays.asList(groupEntity1, groupEntity2, groupEntity3));

    // When mapped
    LocalUserEntity actualEntity = mapper.map(expectedDto);
    LocalUserDto actualDto = mapper.map(expectedEntity);

    // Then they match
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
    assertThat(actualEntity).usingRecursiveComparison().isEqualTo(expectedEntity);

  }

}
