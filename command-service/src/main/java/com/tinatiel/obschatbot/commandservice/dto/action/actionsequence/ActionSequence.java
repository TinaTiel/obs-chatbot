package com.tinatiel.obschatbot.commandservice.dto.action.actionsequence;

import com.tinatiel.obschatbot.commandservice.dto.action.Action;
import java.util.List;

public interface ActionSequence {
  List<Action> getActions();
  void setActions(List<Action> actions);
}
