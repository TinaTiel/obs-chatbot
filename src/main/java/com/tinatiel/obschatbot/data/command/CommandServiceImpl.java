package com.tinatiel.obschatbot.data.command;

import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.data.command.mapper.executable.ExecutableCommandMapper;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import java.util.Optional;

/**
 * A temporary in-memory implementation currently being used for sanity-checking. Replace with JPA!
 */
public class CommandServiceImpl implements CommandService {

  private final OwnerService ownerService;
  private final CommandEntityService commandEntityService;
  private final ExecutableCommandMapper mapper;

  public CommandServiceImpl(OwnerService ownerService,
    CommandEntityService commandEntityService,
    ExecutableCommandMapper mapper) {
    this.ownerService = ownerService;
    this.commandEntityService = commandEntityService;
    this.mapper = mapper;
  }

  @Override
  public Optional<Command> findByName(String name) {
    return commandEntityService.findByNameAndOwner(name, ownerService.getOwner().getId())
      .flatMap(it -> Optional.of(mapper.map(it)));
  }

}
