package com.tinatiel.obschatbot.data.client.obs;

import com.tinatiel.obschatbot.data.client.obs.model.ObsSettingsDto;
import com.tinatiel.obschatbot.data.error.DataPersistenceException;
import java.util.Optional;
import java.util.UUID;

public class ObsClientDataServiceImpl implements ObsClientDataService {

  @Override
  public ObsSettingsDto save(ObsSettingsDto dto) throws DataPersistenceException {
    return null;
  }

  @Override
  public Optional<ObsSettingsDto> findByOwner(UUID owner) {
    return Optional.empty();
  }
}
