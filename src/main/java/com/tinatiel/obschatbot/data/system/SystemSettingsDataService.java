package com.tinatiel.obschatbot.data.system;

import com.tinatiel.obschatbot.data.error.DataPersistenceException;
import com.tinatiel.obschatbot.data.system.model.SystemSettingsDto;
import java.util.Optional;
import java.util.UUID;

public interface SystemSettingsDataService {

  SystemSettingsDto save(SystemSettingsDto dto) throws DataPersistenceException;
  Optional<SystemSettingsDto> findByOwner(UUID owner);

}
