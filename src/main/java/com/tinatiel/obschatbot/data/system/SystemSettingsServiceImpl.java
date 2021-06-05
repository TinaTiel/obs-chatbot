package com.tinatiel.obschatbot.data.system;

import com.tinatiel.obschatbot.data.error.DataPersistenceException;
import com.tinatiel.obschatbot.data.system.entity.SystemSettingsRepository;
import com.tinatiel.obschatbot.data.system.mapper.SystemSettingsMapper;
import com.tinatiel.obschatbot.data.system.model.SystemSettingsDto;
import java.util.Optional;
import java.util.UUID;

public class SystemSettingsServiceImpl implements SystemSettingsService {

  private final SystemSettingsRepository repository;
  private final SystemSettingsMapper mapper;

  public SystemSettingsServiceImpl(
    SystemSettingsRepository repository,
    SystemSettingsMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public SystemSettingsDto save(SystemSettingsDto dto) throws DataPersistenceException {
    // If this is new but the owner already has an entry, throw exception
//    if(dto != null
//        && dto.getOwner() != null
//        && dto.getId() == null
//        && repository.existsByOwner(dto.getOwner())
//    ) {
//      throw new DataPersistenceException("Only one setting is permitted per owner");
//    }

    // Otherwise save
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
