package com.tinatiel.obschatbot.commandservice.service;

import com.tinatiel.obschatbot.commandservice.dto.CommandDto;
import reactor.core.publisher.Flux;

public interface CommandFlattenerService {
  Flux<CommandDto> flatten(CommandDto commandDto) throws CyclicalReferenceException;
}
