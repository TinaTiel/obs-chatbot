package com.tinatiel.obschatbot.data.client.twitch;

import com.tinatiel.obschatbot.data.client.twitch.model.TwitchClientDataDto;
import com.tinatiel.obschatbot.data.error.DataPersistenceException;
import java.util.Optional;
import java.util.UUID;

public class TwitchClientDataServiceImpl implements TwitchClientDataService {

  @Override
  public TwitchClientDataDto save(TwitchClientDataDto dto) throws DataPersistenceException {
    return null;
  }

  @Override
  public Optional<TwitchClientDataDto> findByOwner(UUID owner) {
    return Optional.empty();
  }
}
