package com.tinatiel.obschatbot.commandservice.dto.action.actionsequence;

import com.tinatiel.obschatbot.commandservice.dto.action.Action;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InOrderActionSequenceGenerator implements ActionSequenceGenerator<InOrderActionSequence> {

  @Override
  public boolean accept(ActionSequence actionSequence) {
    return actionSequence instanceof InOrderActionSequence;
  }

  @Override
  public List<Action> generate(InOrderActionSequence actionSequence) {
    List<Action> actions = new ArrayList<>(actionSequence.getActions());
    if(actionSequence.isReverse()) {
      Collections.reverse(actions);
    }
    return actions;
  }
}
