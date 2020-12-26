package com.tinatiel.obschatbot.core.command;

import java.util.Optional;

public interface CommandRepository {
    Optional<Command> findByName(String name);
}
