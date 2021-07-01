package com.tinatiel.obschatbot.data.client.obs;

import com.tinatiel.obschatbot.data.client.obs.entity.ObsClientDataRepository;
import com.tinatiel.obschatbot.data.client.obs.mapper.ObsClientDataMapper;
import com.tinatiel.obschatbot.data.client.obs.model.ObsClientSettingsDto;
import com.tinatiel.obschatbot.data.error.DataPersistenceException;
import java.util.Optional;
import java.util.UUID;

public class ObsClientDataServiceImpl implements ObsClientDataService {

  private final ObsClientDataRepository repository;
  private final ObsClientDataMapper mapper;

  public ObsClientDataServiceImpl(
    ObsClientDataRepository repository,
    ObsClientDataMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public ObsClientSettingsDto save(ObsClientSettingsDto dto) throws DataPersistenceException {
    try {
      return mapper.map(repository.save(mapper.map(dto)));
    } catch (Exception e) {
      throw new DataPersistenceException("Could not save " + dto, e);
    }
  }

  @Override
  public Optional<ObsClientSettingsDto> findByOwner(UUID owner) {
    return repository.findByOwner(owner).flatMap(it -> Optional.of(mapper.map(it)));
  }
}
