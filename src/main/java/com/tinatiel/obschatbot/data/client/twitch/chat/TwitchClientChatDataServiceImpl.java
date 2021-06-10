package com.tinatiel.obschatbot.data.client.twitch.chat;

import com.tinatiel.obschatbot.data.client.twitch.chat.entity.TwitchClientChatDataRepository;
import com.tinatiel.obschatbot.data.client.twitch.chat.mapper.TwitchClientChatDataMapper;
import com.tinatiel.obschatbot.data.client.twitch.chat.model.TwitchClientChatDataDto;
import com.tinatiel.obschatbot.data.error.DataPersistenceException;
import java.util.Optional;
import java.util.UUID;

public class TwitchClientChatDataServiceImpl implements TwitchClientChatDataService {

  private final TwitchClientChatDataRepository repository;
  private final TwitchClientChatDataMapper mapper;

  public TwitchClientChatDataServiceImpl(
    TwitchClientChatDataRepository repository,
    TwitchClientChatDataMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public TwitchClientChatDataDto save(TwitchClientChatDataDto dto) throws DataPersistenceException {
    try {
      return mapper.map(repository.save(mapper.map(dto)));
    } catch (Exception e) {
      throw new DataPersistenceException("Could not save " + dto, e);
    }
  }

  @Override
  public Optional<TwitchClientChatDataDto> findByOwner(UUID owner) {
    return repository.findByOwner(owner).flatMap(it -> Optional.of(mapper.map(it)));
  }
}
