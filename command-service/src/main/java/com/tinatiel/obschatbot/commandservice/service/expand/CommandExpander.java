package com.tinatiel.obschatbot.commandservice.service.expand;

import com.tinatiel.obschatbot.commandservice.dto.CommandDto;
import com.tinatiel.obschatbot.commandservice.dto.action.ExecuteSequenceAction;
import com.tinatiel.obschatbot.commandservice.dto.action.actionsequence.ActionSequence;
import com.tinatiel.obschatbot.commandservice.service.CyclicalReferenceException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CommandExpander {

  public CommandDto expand(CommandDto original, Map<UUID, CommandDto> commandMap) throws CyclicalReferenceException {
    CommandDto copy = original.toBuilder().build();
    Set<UUID> visited = new HashSet<>();
    visited.add(original.getId());
    copy.getActionSequence().getActions().forEach(action -> {
      if(action instanceof ExecuteSequenceAction) {
        expand((ExecuteSequenceAction) action, commandMap, visited);
      }
    });
    return copy;
  }

  private void expand(ExecuteSequenceAction sequenceAction, Map<UUID, CommandDto> commandMap, Set<UUID> visited) {
    // Check for cyclical references
    if(visited.contains(sequenceAction.getCommandId())) {
      throw new CyclicalReferenceException(String.format(
        "command %s already visited: %s", sequenceAction.getCommandId(), visited
      ));
    } else {
      visited.add(sequenceAction.getCommandId());
    }

    // expand
    if (commandMap.containsKey(sequenceAction.getCommandId())) {
      ActionSequence sequence = commandMap.get(sequenceAction.getCommandId()).getActionSequence();
      sequenceAction.setActionSequence(sequence);
      sequence.getActions().forEach(action -> {
        if(action instanceof ExecuteSequenceAction) {
          expand((ExecuteSequenceAction) action, commandMap, visited);
        }
      });
    }

  }

}
