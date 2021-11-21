package com.tinatiel.obschatbot.commandservice.args;

import com.tinatiel.obschatbot.commandservice.action.Action;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActionsArgsProcessorFactoryConfig {

  @Bean
  ActionArgsProcessorFactory actionArgsProcessorFactory() {
    Map<Class<? extends Action>, ActionArgsProcessor<?>> processorMap = Map.of();
    return new ActionArgsProcessorFactoryImpl(processorMap);
  }

}
