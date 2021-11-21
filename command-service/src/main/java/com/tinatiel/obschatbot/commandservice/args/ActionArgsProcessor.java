package com.tinatiel.obschatbot.commandservice.args;

import com.tinatiel.obschatbot.commandservice.action.Action;

public interface ActionArgsProcessor<T extends Action> {
  T process(T action, CommandArgs commandArgs);
}
