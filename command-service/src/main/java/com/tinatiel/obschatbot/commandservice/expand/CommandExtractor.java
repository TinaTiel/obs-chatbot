package com.tinatiel.obschatbot.commandservice.expand;

import com.tinatiel.obschatbot.commandservice.command.CommandDto;
import com.tinatiel.obschatbot.commandservice.action.system.ExecuteSequenceAction;
import com.tinatiel.obschatbot.commandservice.command.CommandService;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Responsible for inspecting a command for any ExecuteSequenceAction actions
 * and fetching the command referenced by it (recursively; so if a command contains an
 * ExecuteSequenceAction that contains one more many more ExecuteSequenceAction, then
 * those command are also returned).
 */
@RequiredArgsConstructor
public class CommandExtractor {

  private final CommandService commandService;

  public Mono<Map<UUID,CommandDto>> extract(CommandDto commandDto) {
    Set<UUID> visited = new HashSet<>();
    return Flux.just(commandDto)
      .expandDeep(it -> this.expandInternal(it, visited))
      .collectMap(CommandDto::getId);
  }

  private Flux<CommandDto> expandInternal(CommandDto commandDto, Set<UUID> visited) {
    if(visited.contains(commandDto.getId())) {
      throw new CyclicalReferenceException(String.format(
        "command %s already visited: %s", commandDto.getId(), visited
      ));
    } else {
      visited.add(commandDto.getId());
    }

    return Flux.fromIterable(commandDto.getActionSequence().getActions())
      .filter(action -> action instanceof ExecuteSequenceAction)
      .cast(ExecuteSequenceAction.class)
      .flatMap(executeSeqAction -> commandService
        .findById(executeSeqAction.getCommandId())
        .switchIfEmpty(Mono.error(new CommandNotFoundException("No command found with id " + executeSeqAction.getCommandId())))
      );
  }

}
