package com.tinatiel.obschatbot.core.command;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CommandRepositoryInMemoryImpl implements CommandRepository{

    private final Set<Command> commands = new HashSet<>();

    @Override
    public Optional<Command> findByName(String name) {
        return commands.stream()
                .filter(it -> it.getName().equals(name))
                .findFirst();
    }

    @Override
    public Command save(Command command) {
        commands.add(command);
        return command;
    }
}
