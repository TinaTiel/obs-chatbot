package com.tinatiel.obschatbot.data.localuser.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.tinatiel.obschatbot.data.localuser.entity.LocalGroupEntity;
import com.tinatiel.obschatbot.data.localuser.model.LocalGroupDto;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class LocalGroupMapperTest {

  LocalGroupMapper mapper = Mappers.getMapper(LocalGroupMapper.class);

  @Test
  void mapLocalGroup() {

    // Given a local group
    LocalGroupDto expectedDto = LocalGroupDto.builder()
      .id(UUID.randomUUID())
      .owner(UUID.randomUUID())
      .name("some group")
      .build();

    LocalGroupEntity expectedEntity = new LocalGroupEntity();
    expectedEntity.setId(expectedDto.getId());
    expectedEntity.setOwner(expectedDto.getOwner());
    expectedEntity.setName("some group");

    // when mapped
    LocalGroupEntity actualEntity = mapper.map(expectedDto);
    LocalGroupDto actualDto = mapper.map(expectedEntity);

    // then it matches both ways
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
    assertThat(actualEntity).usingRecursiveComparison().isEqualTo(expectedEntity);

  }

}
