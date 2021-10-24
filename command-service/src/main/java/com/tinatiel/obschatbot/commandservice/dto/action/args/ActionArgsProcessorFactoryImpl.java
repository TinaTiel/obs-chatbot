package com.tinatiel.obschatbot.commandservice.dto.action.args;

import com.tinatiel.obschatbot.commandservice.dto.CommandArgs;
import com.tinatiel.obschatbot.commandservice.dto.action.Action;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ActionArgsProcessorFactoryImpl implements ActionArgsProcessorFactory {

  private final Map<Class<? extends Action>, ActionArgsProcessor<?>> processorMap;

  @Override
  public Action process(Action action, CommandArgs commandArgs) {

    ActionArgsProcessor processor = processorMap.get(action.getClass());
    if(processor != null) {
      return processor.process(action, commandArgs);
    } else {
      return action;
    }

  }

}
