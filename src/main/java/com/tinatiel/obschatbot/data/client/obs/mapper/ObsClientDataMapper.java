package com.tinatiel.obschatbot.data.client.obs.mapper;

import com.tinatiel.obschatbot.data.client.obs.entity.ObsClientSettingsEntity;
import com.tinatiel.obschatbot.data.client.obs.model.ObsClientSettingsDto;
import org.mapstruct.Mapper;

@Mapper
public interface ObsClientDataMapper {
  ObsClientSettingsEntity map(ObsClientSettingsDto dto);
  ObsClientSettingsDto map(ObsClientSettingsEntity entity);
}
