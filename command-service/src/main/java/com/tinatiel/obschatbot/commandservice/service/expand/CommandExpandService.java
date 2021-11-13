package com.tinatiel.obschatbot.commandservice.service.expand;

import com.tinatiel.obschatbot.commandservice.dto.CommandDto;
import java.util.Map;
import java.util.UUID;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommandExpandService {
  Mono<CommandDto> expand(CommandDto commandDto);
}
