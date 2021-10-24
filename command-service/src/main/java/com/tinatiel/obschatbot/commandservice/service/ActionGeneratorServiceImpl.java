package com.tinatiel.obschatbot.commandservice.service;

import com.tinatiel.obschatbot.commandservice.dto.CommandArgs;
import com.tinatiel.obschatbot.commandservice.dto.CommandDto;
import com.tinatiel.obschatbot.commandservice.dto.action.Action;
import com.tinatiel.obschatbot.commandservice.dto.action.ExecuteCommandAction;
import com.tinatiel.obschatbot.commandservice.dto.action.actionsequence.ActionSequence;
import com.tinatiel.obschatbot.commandservice.dto.action.actionsequence.ActionSequenceGenerator;
import com.tinatiel.obschatbot.commandservice.dto.action.actionsequence.UnknownActionSequenceException;
import com.tinatiel.obschatbot.commandservice.dto.action.args.ActionArgsFactory;
import java.util.ArrayList;
import java.util.List;

public class ActionGeneratorServiceImpl implements ActionGeneratorService {

  private final ActionArgsFactory actionArgsFactory;
  private final List<ActionSequenceGenerator<?>> generators;

  public ActionGeneratorServiceImpl(
    ActionArgsFactory actionArgsFactory,
    List<ActionSequenceGenerator<?>> generators) {
    if(generators == null || generators.isEmpty()) {
      throw new IllegalArgumentException("Generators cannot be null");
    }
    this.actionArgsFactory = actionArgsFactory;
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
    List<Action> generatedActions = findFirstGenerator(sequence).generate(sequence);
    for (Action action:generatedActions) {
      if(action instanceof ExecuteCommandAction) {
        this.generate(accumulator, ((ExecuteCommandAction) action).getActionSequence(), commandArgs);
      } else {
        accumulator.add(actionArgsFactory.process(action, commandArgs));
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
