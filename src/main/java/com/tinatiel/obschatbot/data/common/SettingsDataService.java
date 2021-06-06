package com.tinatiel.obschatbot.data.common;

import com.tinatiel.obschatbot.data.error.DataPersistenceException;
import java.util.Optional;
import java.util.UUID;

public interface SettingsDataService<T> {
  T save(T dto) throws DataPersistenceException;
  Optional<T> findByOwner(UUID owner);
}
