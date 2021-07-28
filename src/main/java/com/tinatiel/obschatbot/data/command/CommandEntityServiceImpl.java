package com.tinatiel.obschatbot.data.command;

import com.tinatiel.obschatbot.core.error.UnexpectedException;
import com.tinatiel.obschatbot.data.command.entity.CommandEntity;
import com.tinatiel.obschatbot.data.command.entity.CommandEntityRepository;
import com.tinatiel.obschatbot.data.command.entity.sequencer.SequencerRepository;
import com.tinatiel.obschatbot.data.command.mapper.CommandMapper;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import com.tinatiel.obschatbot.data.error.DataPersistenceException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

/**
 * Default implementation of CommandEntityService.
 */
@Transactional
public class CommandEntityServiceImpl implements CommandEntityService {

  private final CommandEntityRepository repository;
  private final SequencerRepository sequencerRepository;
  private final CommandMapper mapper;

  public CommandEntityServiceImpl(
    CommandEntityRepository repository,
    SequencerRepository sequencerRepository,
    CommandMapper mapper) {
    this.repository = repository;
    this.sequencerRepository = sequencerRepository;
    this.mapper = mapper;
  }

  @Override
  public CommandDto save(CommandDto request) throws DataPersistenceException {

    // Map the request to an entity
    CommandEntity requestEntity = mapper.map(request);

    try {
      CommandEntity result = repository.save(
        requestEntity
      );
      repository.flush();
      return mapper.map(result);
    } catch (Exception e) {
      throw DataPersistenceException.fromObject(request, e);
    }

  }

  @Override
  public Optional<CommandDto> findById(UUID id) {
    return repository.findById(id).flatMap(entity -> Optional.of(mapper.map(entity)));
  }

  @Override
  public Optional<CommandDto> findByNameAndOwner(String name, UUID owner) {
    return repository.findByNameAndOwner(name, owner).flatMap(entity -> Optional.of(mapper.map(entity)));
  }

  @Override
  public List<CommandDto> findByOwner(UUID owner) {
    return repository.findByOwner(owner).stream().map(mapper::map).collect(Collectors.toList());
  }

  @Override
  public void delete(UUID id) throws DataPersistenceException {
    repository.delete(repository.findById(id).orElseThrow(() ->
      new DataPersistenceException("Did not find existing command with id " + id)
    ));
  }
}
