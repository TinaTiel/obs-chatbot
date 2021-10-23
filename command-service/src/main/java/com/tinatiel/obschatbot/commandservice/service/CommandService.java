package com.tinatiel.obschatbot.commandservice.service;

import java.util.UUID;
import reactor.core.publisher.Mono;
import com.tinatiel.obschatbot.commandservice.dto.CommandDto;

public interface CommandService {
  Mono<CommandDto> findByNameAndOwner(String name, UUID ownerId);
  Mono<CommandDto> findById(UUID id);
  Mono<CommandDto> save(CommandDto commandDto);
  Mono<Void> deleteById(UUID id);
}
