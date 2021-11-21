package com.tinatiel.obschatbot.commandservice.command;

import java.util.UUID;
import reactor.core.publisher.Mono;

public interface CommandService {
  Mono<CommandDto> findByNameAndOwner(String name, UUID ownerId);
  Mono<CommandDto> findById(UUID id);
  Mono<CommandDto> save(CommandDto commandDto);
  Mono<Void> deleteById(UUID id);
}
