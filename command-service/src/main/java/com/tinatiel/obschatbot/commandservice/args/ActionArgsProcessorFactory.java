package com.tinatiel.obschatbot.commandservice.args;

import com.tinatiel.obschatbot.commandservice.action.Action;

public interface ActionArgsProcessorFactory {
  Action process(Action action, CommandArgs commandArgs);
}
