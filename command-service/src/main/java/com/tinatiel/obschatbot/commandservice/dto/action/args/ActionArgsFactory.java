package com.tinatiel.obschatbot.commandservice.dto.action.args;

import com.tinatiel.obschatbot.commandservice.dto.CommandArgs;
import com.tinatiel.obschatbot.commandservice.dto.action.Action;

public interface ActionArgsFactory {
  Action process(Action action, CommandArgs commandArgs);
}
