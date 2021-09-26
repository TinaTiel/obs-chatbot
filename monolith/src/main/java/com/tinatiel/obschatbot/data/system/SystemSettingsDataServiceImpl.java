package com.tinatiel.obschatbot.data.system;

import com.tinatiel.obschatbot.data.error.DataPersistenceException;
import com.tinatiel.obschatbot.data.system.entity.SystemSettingsRepository;
import com.tinatiel.obschatbot.data.system.mapper.SystemSettingsMapper;
import com.tinatiel.obschatbot.data.system.model.SystemSettingsDto;
import java.util.Optional;
import java.util.UUID;

public class SystemSettingsDataServiceImpl implements SystemSettingsDataService {

  private final SystemSettingsRepository repository;
  private final SystemSettingsMapper mapper;

  public SystemSettingsDataServiceImpl(
    SystemSettingsRepository repository,
    SystemSettingsMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public SystemSettingsDto save(SystemSettingsDto dto) throws DataPersistenceException {
    try{
      return mapper.map(repository.saveAndFlush(mapper.map(dto)));
    } catch (Exception e) {
      throw new DataPersistenceException("Could not save " + dto, e);
    }
  }

  @Override
  public Optional<SystemSettingsDto> findByOwner(UUID owner) {
    return repository.findByOwner(owner).flatMap(it -> Optional.of(mapper.map(it)));
  }

}
