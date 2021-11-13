package com.tinatiel.obschatbot.commandservice.service.expand;

import com.tinatiel.obschatbot.commandservice.service.CommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandExpandServiceConfig {

  @Autowired
  CommandService commandService;

  @Bean
  CommandExtractor commandExpandFunction() {
    return new CommandExtractor(commandService);
  }

  @Bean
  CommandExpander commandFromMapExpander() {
    return new CommandExpander();
  }

  @Bean
  CommandExpandService commandExpandService() {
    return new CommandExpandServiceImpl(
      commandExpandFunction(), commandFromMapExpander()
    );
  }

}
