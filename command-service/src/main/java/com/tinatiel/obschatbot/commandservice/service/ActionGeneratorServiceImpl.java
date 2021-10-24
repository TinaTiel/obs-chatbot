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
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ActionGeneratorServiceImpl implements ActionGeneratorService {

  private final ActionArgsFactory actionArgsFactory;
  private final Map<Class<? extends ActionSequence>, ActionSequenceGenerator<?>> generators;

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
    List<Action> generatedActions = getGenerator(sequence).generate(sequence);
    for (Action action:generatedActions) {
      if(action instanceof ExecuteCommandAction) {
        this.generate(accumulator, ((ExecuteCommandAction) action).getActionSequence(), commandArgs);
      } else {
        accumulator.add(actionArgsFactory.process(action, commandArgs));
      }
    }
  }

  private ActionSequenceGenerator getGenerator(ActionSequence sequence) {
    if (generators.containsKey(sequence.getClass())) {
      return generators.get(sequence.getClass());
    } else {
      throw new UnknownActionSequenceException(
        "No generator available for action sequence " + sequence.getClass()
      );
    }
  }

}
