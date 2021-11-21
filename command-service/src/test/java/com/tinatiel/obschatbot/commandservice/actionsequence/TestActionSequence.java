package com.tinatiel.obschatbot.commandservice.actionsequence;

import com.tinatiel.obschatbot.commandservice.action.Action;
import java.util.ArrayList;
import java.util.List;

/**
 * An action sequence that returns the actions as they are, no other bells and whistles.
 */
public class TestActionSequence implements ActionSequence {

  private List<Action> actions;

  public TestActionSequence(List<Action> actions) {
    this.actions = actions;
  }

  @Override
  public List<Action> getActions() {
    return new ArrayList(actions);
  }

  @Override
  public void setActions(List<Action> actions) {
    this.actions = actions;
  }

  @Override
  public String toString() {
    return "TestActionSequence{"
      + "actions=" + actions
      + '}';
  }
}
