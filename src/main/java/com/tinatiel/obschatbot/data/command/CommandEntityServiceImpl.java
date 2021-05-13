package com.tinatiel.obschatbot.data.command;

import com.tinatiel.obschatbot.data.command.entity.CommandEntityRepository;
import com.tinatiel.obschatbot.data.command.mapper.CommandMapper;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CommandEntityServiceImpl implements CommandEntityService {

  private final CommandEntityRepository commandEntityRepository;
  private final CommandMapper mapper;

  public CommandEntityServiceImpl(
    CommandEntityRepository commandEntityRepository,
    CommandMapper mapper) {
    this.commandEntityRepository = commandEntityRepository;
    this.mapper = mapper;
  }

  @Override
  public CommandDto save(CommandDto commandEntity) {
    return null;
  }

  @Override
  public Optional<CommandDto> findById(UUID id) {
    return Optional.empty();
  }

  @Override
  public Optional<CommandDto> findByName(String name) {
    return Optional.empty();
  }

  @Override
  public List<CommandDto> findAll() {
    return null;
  }

  @Override
  public void delete(UUID id) {

  }
}
