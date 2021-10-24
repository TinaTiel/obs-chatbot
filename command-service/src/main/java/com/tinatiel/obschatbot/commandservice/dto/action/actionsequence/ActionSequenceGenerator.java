package com.tinatiel.obschatbot.commandservice.dto.action.actionsequence;

import com.tinatiel.obschatbot.commandservice.dto.action.Action;
import java.util.List;

public interface ActionSequenceGenerator<T> {
  boolean accept(ActionSequence actionSequence);
  List<Action> generate(T actionSequence);
}
