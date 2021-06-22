package com.tinatiel.obschatbot.data.localuser.mapper;

import com.tinatiel.obschatbot.data.localuser.entity.LocalUserEntity;
import com.tinatiel.obschatbot.data.localuser.model.LocalUserDto;
import java.util.UUID;
import org.mapstruct.Mapper;

@Mapper(imports = {UUID.class})
public interface LocalUserMapper {
  LocalUserEntity map(LocalUserDto dto);
  LocalUserDto map(LocalUserEntity entity);
}
