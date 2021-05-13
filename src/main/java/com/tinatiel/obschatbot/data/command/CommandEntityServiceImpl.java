package com.tinatiel.obschatbot.data.command;

import com.tinatiel.obschatbot.data.command.entity.CommandEntity;
import com.tinatiel.obschatbot.data.command.entity.CommandEntityRepository;
import com.tinatiel.obschatbot.data.command.mapper.CommandMapper;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
  public CommandDto save(CommandDto commandEntity) {
    CommandEntity entity = repository.saveAndFlush(
      mapper.dtoToEntity(commandEntity)
    );
    return mapper.entityToDto(entity);
  }

  @Override
  public Optional<CommandDto> findById(UUID id) {
    return repository.findById(id).flatMap(entity -> Optional.of(mapper.entityToDto(entity)));
  }

  @Override
  public Optional<CommandDto> findByName(String name) {
    return repository.findByName(name).flatMap(entity -> Optional.of(mapper.entityToDto(entity)));
  }

  @Override
  public List<CommandDto> findAll() {
    return null;
  }

  @Override
  public void delete(UUID id) {

  }
}
