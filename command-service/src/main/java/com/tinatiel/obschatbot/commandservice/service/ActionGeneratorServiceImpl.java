package com.tinatiel.obschatbot.commandservice.service;

import com.tinatiel.obschatbot.commandservice.dto.CommandArgs;
import com.tinatiel.obschatbot.commandservice.dto.CommandDto;
import com.tinatiel.obschatbot.commandservice.dto.action.Action;
import com.tinatiel.obschatbot.commandservice.dto.action.ExecuteCommandAction;
import com.tinatiel.obschatbot.commandservice.dto.actionsequence.ActionSequence;
import com.tinatiel.obschatbot.commandservice.dto.actionsequence.ActionSequenceGenerator;
import com.tinatiel.obschatbot.commandservice.dto.actionsequence.UnknownActionSequenceException;
import java.util.ArrayList;
import java.util.List;

public class ActionGeneratorServiceImpl implements ActionGeneratorService {

  private final List<ActionSequenceGenerator<?>> generators;

  public ActionGeneratorServiceImpl(
    List<ActionSequenceGenerator<?>> generators) {
    if(generators == null || generators.isEmpty()) {
      throw new IllegalArgumentException("Generators cannot be null");
    }
    this.generators = generators;
  }

  @Override
  public List<Action> generate(CommandDto commandDto, CommandArgs commandArgs) {
    // Exit early if there is no sequence
    if(commandDto != null && commandDto.getActionSequence() == null) {
      return new ArrayList<>();
    }

    // Otherwise build and accumulate the list recursively
    List<Action> result = new ArrayList<>();
    generate(result, commandDto.getActionSequence(), commandArgs);
    return result;
  }

  private void generate(List<Action> accumulator, ActionSequence sequence, CommandArgs commandArgs) {
    List<Action> generatedActions = findFirstGenerator(sequence).generate(sequence, commandArgs);
    for (Action action:generatedActions) {
      if(action instanceof ExecuteCommandAction) {
        this.generate(accumulator, ((ExecuteCommandAction) action).getActionSequence(), commandArgs);
      } else {
        accumulator.add(action);
      }
    }
  }

  private ActionSequenceGenerator findFirstGenerator(ActionSequence sequence) {
    return generators.stream()
      .filter(it -> it.accept(sequence))
      .findFirst()
      .orElseThrow(() -> new UnknownActionSequenceException(
        "No generator available for action sequence " + sequence.getClass())
      );
  }

}
