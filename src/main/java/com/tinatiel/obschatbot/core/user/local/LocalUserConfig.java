package com.tinatiel.obschatbot.core.user.local;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the local user package.
 */
@Configuration
public class LocalUserConfig {

  @Bean
  LocalUserRepository localUserRepository() {
    return new LocalUserRepositoryInMemoryImpl();
  }

}
