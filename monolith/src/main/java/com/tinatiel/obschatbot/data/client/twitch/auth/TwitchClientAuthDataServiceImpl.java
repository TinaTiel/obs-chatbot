package com.tinatiel.obschatbot.data.client.twitch.auth;

import com.tinatiel.obschatbot.data.client.twitch.auth.entity.TwitchClientAuthDataRepository;
import com.tinatiel.obschatbot.data.client.twitch.auth.mapper.TwitchClientAuthDataMapper;
import com.tinatiel.obschatbot.data.client.twitch.auth.model.TwitchClientAuthDataDto;
import com.tinatiel.obschatbot.data.error.DataPersistenceException;
import java.util.Optional;
import java.util.UUID;

public class TwitchClientAuthDataServiceImpl implements TwitchClientAuthDataService {

  private final TwitchClientAuthDataRepository repository;
  private final TwitchClientAuthDataMapper mapper;

  public TwitchClientAuthDataServiceImpl(
    TwitchClientAuthDataRepository repository,
    TwitchClientAuthDataMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public TwitchClientAuthDataDto save(TwitchClientAuthDataDto dto) throws DataPersistenceException {
    try {
      return mapper.map(repository.save(mapper.map(dto)));
    } catch (Exception e) {
      throw new DataPersistenceException("Could not save " + dto, e);
    }
  }

  @Override
  public Optional<TwitchClientAuthDataDto> findByOwner(UUID owner) {
    return repository.findByOwner(owner).flatMap(it -> Optional.of(mapper.map(it)));
  }
}
