package com.tinatiel.obschatbot.data.client.twitch;

import com.tinatiel.obschatbot.data.client.twitch.entity.TwitchClientDataRepository;
import com.tinatiel.obschatbot.data.client.twitch.mapper.TwitchClientDataMapper;
import com.tinatiel.obschatbot.data.client.twitch.model.TwitchClientDataDto;
import com.tinatiel.obschatbot.data.error.DataPersistenceException;
import java.util.Optional;
import java.util.UUID;

public class TwitchClientDataServiceImpl implements TwitchClientDataService {

  private final TwitchClientDataRepository repository;
  private final TwitchClientDataMapper mapper;

  public TwitchClientDataServiceImpl(
    TwitchClientDataRepository repository,
    TwitchClientDataMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public TwitchClientDataDto save(TwitchClientDataDto dto) throws DataPersistenceException {
    try {
      return mapper.map(repository.save(mapper.map(dto)));
    } catch (Exception e) {
      throw new DataPersistenceException("Could not save " + dto, e);
    }
  }

  @Override
  public Optional<TwitchClientDataDto> findByOwner(UUID owner) {
    return repository.findByOwner(owner).flatMap(it -> Optional.of(mapper.map(it)));
  }
}
