package com.tinatiel.obschatbot.core.command;

import com.tinatiel.obschatbot.data.command.CommandEntityService;
import com.tinatiel.obschatbot.data.command.mapper.executable.ExecutableCommandMapper;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * A temporary in-memory implementation currently being used for sanity-checking. Replace with JPA!
 */
public class CommandServiceImpl implements CommandService {

  private final CommandEntityService commandEntityService;
  private final ExecutableCommandMapper mapper;

  public CommandServiceImpl(
    CommandEntityService commandEntityService,
    ExecutableCommandMapper mapper) {
    this.commandEntityService = commandEntityService;
    this.mapper = mapper;
  }

  @Override
  public Optional<Command> findByName(String name) {
    return commandEntityService.findByName(name)
      .flatMap(it -> Optional.of(mapper.map(it)));
  }

}
