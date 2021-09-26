package com.tinatiel.obschatbot.data.system.mapper;

import com.tinatiel.obschatbot.data.system.entity.SystemSettingsEntity;
import com.tinatiel.obschatbot.data.system.model.SystemSettingsDto;
import java.util.UUID;
import org.mapstruct.Mapper;

@Mapper(imports = UUID.class)
public interface SystemSettingsMapper {

  SystemSettingsDto map(SystemSettingsEntity entity);
  SystemSettingsEntity map(SystemSettingsDto dto);

}
