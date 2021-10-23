package com.tinatiel.obschatbot.commandservice.actionsequence;

import com.tinatiel.obschatbot.commandservice.dto.action.Action;
import com.tinatiel.obschatbot.commandservice.dto.actionsequence.ActionSequence;
import java.util.ArrayList;
import java.util.List;

/**
 * Action sequence with no strings attached; just a list of actions.
 */
public class TestActionSequence implements ActionSequence {

  private final List<Action> actions;

  public TestActionSequence(List<Action> actions) {
    this.actions = actions;
  }

  @Override
  public List<Action> getActions() {
    return new ArrayList(actions);
  }
}
