package com.tinatiel.obschatbot.data.system;

import com.tinatiel.obschatbot.data.system.model.SystemSettingsDto;
import java.util.Optional;
import java.util.UUID;

public interface SystemSettingsService {

  SystemSettingsDto save(SystemSettingsDto dto);
  Optional<SystemSettingsDto> findByOwner(UUID owner);

}
