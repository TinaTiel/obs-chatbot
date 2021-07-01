package com.tinatiel.obschatbot.data.localuser.mapper;

import com.tinatiel.obschatbot.data.localuser.entity.LocalGroupEntity;
import com.tinatiel.obschatbot.data.localuser.model.LocalGroupDto;
import java.util.UUID;
import org.mapstruct.Mapper;

@Mapper(imports = {UUID.class})
public interface LocalGroupMapper {
  LocalGroupEntity map(LocalGroupDto dto);
  LocalGroupDto map(LocalGroupEntity entity);
}
