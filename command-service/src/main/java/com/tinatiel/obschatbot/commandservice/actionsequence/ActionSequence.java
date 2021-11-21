package com.tinatiel.obschatbot.commandservice.actionsequence;

import com.tinatiel.obschatbot.commandservice.action.Action;
import java.util.List;

public interface ActionSequence {
  List<Action> getActions();
  void setActions(List<Action> actions);
}
