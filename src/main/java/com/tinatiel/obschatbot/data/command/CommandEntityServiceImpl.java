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
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

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
      CommandEntity entity = repository.saveAndFlush(
        mapper.dtoToEntity(request)
      );
        return mapper.entityToDto(entity);
      } catch (Exception e) {
        throw new DataPersistenceException(request, e);
      }
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
    return repository.findAll().stream().map(mapper::entityToDto).collect(Collectors.toList());
  }

  @Override
  public void delete(UUID id) throws DataPersistenceException {

  }
}
