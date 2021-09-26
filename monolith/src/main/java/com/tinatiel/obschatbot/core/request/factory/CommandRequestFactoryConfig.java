package com.tinatiel.obschatbot.core.request.factory;

import com.tinatiel.obschatbot.core.request.factory.expand.CommandExpander;
import com.tinatiel.obschatbot.core.request.factory.expand.CommandExpanderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Encompasses all configuration for the CommandRequestFactory instance.
 */
@Import(CommandExpanderConfig.class)
@Configuration
public class CommandRequestFactoryConfig {

  @Autowired
  CommandExpander commandExpander;

  @Bean
  CommandRequestFactory requestFactory() {
    return new CommandRequestFactoryImpl(
      commandExpander
    );
  }
}
