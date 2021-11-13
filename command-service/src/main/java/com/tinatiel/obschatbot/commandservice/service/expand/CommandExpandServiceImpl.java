package com.tinatiel.obschatbot.commandservice.service.expand;

import com.tinatiel.obschatbot.commandservice.dto.CommandDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class CommandExpandServiceImpl implements CommandExpandService {

  private final CommandExtractor commandExtractor;
  private final CommandExpander commandExpander;

  @Override
  public Mono<CommandDto> expand(CommandDto commandDto) {
    return commandExtractor
      .extract(commandDto)
      .map(commandMap -> commandExpander.expand(commandDto, commandMap));
  }

}
