package com.tinatiel.obschatbot.commandservice.dto.action.args;

import com.tinatiel.obschatbot.commandservice.dto.CommandArgs;
import com.tinatiel.obschatbot.commandservice.dto.action.Action;

public interface ActionArgsProcessor<T extends Action> {
  T process(T action, CommandArgs commandArgs);
}
