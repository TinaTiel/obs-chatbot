package com.tinatiel.obschatbot.data.owner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OwnerConfig {

  @Bean
  OwnerService ownerService() {
    return new SystemOwnerService();
  }

}
