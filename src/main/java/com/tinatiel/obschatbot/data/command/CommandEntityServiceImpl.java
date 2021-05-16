package com.tinatiel.obschatbot.data.command;

import com.tinatiel.obschatbot.data.command.entity.CommandEntity;
import com.tinatiel.obschatbot.data.command.entity.CommandEntityRepository;
import com.tinatiel.obschatbot.data.command.mapper.CommandMapper;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import com.tinatiel.obschatbot.data.error.DataPersistenceException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Default implementation of CommandEntityService.
 */
public class CommandEntityServiceImpl implements CommandEntityService {

  private final CommandEntityRepository repository;
  private final CommandMapper mapper;

  public CommandEntityServiceImpl(
      CommandEntityRepository repository,
      CommandMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public CommandDto save(CommandDto request) throws DataPersistenceException {
    try {
      CommandEntity requestEntity = mapper.map(request);
      if(requestEntity.getSequencer() != null) {
        requestEntity.getSequencer().setCommand(requestEntity); // set other side of relationship
      }
      CommandEntity entity = repository.save(
          requestEntity
      );
      repository.flush();
      return mapper.map(entity);
    } catch (Exception e) {
      throw new DataPersistenceException(request, e);
    }
  }

  @Override
  public Optional<CommandDto> findById(UUID id) {
    return repository.findById(id).flatMap(entity -> Optional.of(mapper.map(entity)));
  }

  @Override
  public Optional<CommandDto> findByName(String name) {
    return repository.findByName(name).flatMap(entity -> Optional.of(mapper.map(entity)));
  }

  @Override
  public List<CommandDto> findAll() {
    return repository.findAll().stream().map(mapper::map).collect(Collectors.toList());
  }

  @Override
  public void delete(UUID id) throws DataPersistenceException {

  }
}
