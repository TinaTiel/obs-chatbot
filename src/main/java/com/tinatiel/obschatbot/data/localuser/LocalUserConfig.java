package com.tinatiel.obschatbot.data.localuser;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the local user package.
 */
@Configuration
public class LocalUserConfig {

  @Bean
  LocalUserService localUserRepository() {
    return new LocalUserServiceImpl();
  }

  @Bean
  LocalGroupService localGroupService() {
    return new LocalGroupServiceImpl();
  }

}
