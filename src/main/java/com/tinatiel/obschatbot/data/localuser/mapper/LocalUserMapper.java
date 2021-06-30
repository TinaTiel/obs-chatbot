package com.tinatiel.obschatbot.data.localuser.mapper;

import com.tinatiel.obschatbot.data.localuser.entity.LocalGroupEntity;
import com.tinatiel.obschatbot.data.localuser.entity.LocalUserEntity;
import com.tinatiel.obschatbot.data.localuser.model.LocalGroupDto;
import com.tinatiel.obschatbot.data.localuser.model.LocalUserDto;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.mapstruct.Mapper;

@Mapper(imports = {UUID.class})
public interface LocalUserMapper {
  LocalUserEntity map(LocalUserDto dto);
  LocalUserDto map(LocalUserEntity entity);

  default List<LocalGroupDto> mapGroupEntities(Set<LocalGroupEntity> groupEntities) {
    List<LocalGroupDto> results = new ArrayList<>();
    if(groupEntities != null) {
      groupEntities.forEach(it -> results.add(map(it)));
    }
    return results;
  }

  default Set<LocalGroupEntity> mapGroupDtos(List<LocalGroupDto> groupDtos) {
    return new HashSet<>();
  }

  LocalGroupDto map(LocalGroupEntity groupEntity);

}
