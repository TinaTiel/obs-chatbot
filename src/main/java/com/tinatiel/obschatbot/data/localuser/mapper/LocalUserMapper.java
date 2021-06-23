package com.tinatiel.obschatbot.data.localuser.mapper;

import com.tinatiel.obschatbot.data.localuser.entity.LocalGroupEntity;
import com.tinatiel.obschatbot.data.localuser.entity.LocalUserEntity;
import com.tinatiel.obschatbot.data.localuser.model.LocalGroupDto;
import com.tinatiel.obschatbot.data.localuser.model.LocalUserDto;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.mapstruct.Mapper;

@Mapper(imports = {UUID.class})
public interface LocalUserMapper {
  LocalUserEntity map(LocalUserDto dto);
  LocalUserDto map(LocalUserEntity entity);
  default Set<LocalGroupEntity> map(List<LocalGroupDto> dtoList) {
    Set<LocalGroupEntity> results = new HashSet<>();
    if(dtoList != null) {
      dtoList.forEach(it -> results.add(map(it)));
    }
    return  results;
  }
  default List<LocalGroupDto> map(Set<LocalGroupEntity> entitySet) {
    List<LocalGroupDto> results = new ArrayList<>();
    if(entitySet != null) {
      entitySet.forEach(it -> results.add(map(it)));
    }
    results.sort(Comparator.comparing(LocalGroupDto::getName));
    return  results;
  }
  LocalGroupEntity map(LocalGroupDto dto);
  LocalGroupDto map(LocalGroupEntity entity);
}
