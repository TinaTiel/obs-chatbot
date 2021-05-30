package com.tinatiel.obschatbot.data.system;

import com.tinatiel.obschatbot.data.system.model.SystemSettingsDto;

public interface SystemSettingsService {

  SystemSettingsDto save(SystemSettingsDto dto);
  SystemSettingsDto getForSystem();

}
