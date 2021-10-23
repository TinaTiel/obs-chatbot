package com.tinatiel.obschatbot.commandservice.dto.actionsequence;

import com.tinatiel.obschatbot.commandservice.dto.action.Action;
import java.util.List;

public interface ActionSequence {
  List<Action> getActions();
}
