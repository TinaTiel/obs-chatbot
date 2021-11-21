package com.tinatiel.obschatbot.commandservice.actionsequence;

import com.tinatiel.obschatbot.commandservice.action.Action;
import java.util.List;

public interface ActionSequenceGenerator<T extends ActionSequence> {
  List<Action> generate(T actionSequence);
}
