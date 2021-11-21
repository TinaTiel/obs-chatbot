package com.tinatiel.obschatbot.commandservice.expand;

import com.tinatiel.obschatbot.commandservice.command.CommandDto;
import reactor.core.publisher.Mono;

public interface CommandExpandService {
  Mono<CommandDto> expand(CommandDto commandDto);
}
