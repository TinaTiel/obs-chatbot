package com.tinatiel.obschatbot.core.command;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * A temporary in-memory implementation currently being used for sanity-checking. Replace with JPA!
 */
public class CommandServiceInMemoryImpl implements CommandService {

  private final Set<Command> commands = new HashSet<>();

  @Override
  public Optional<Command> findByName(String name) {
    return commands.stream()
      .filter(it -> it.getName().equals(name))
      .findFirst();
  }

  public Command save(Command command) {
    commands.add(command);
    return command;
  }

}
