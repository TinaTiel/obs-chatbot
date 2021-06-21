package com.tinatiel.obschatbot.data.client.obs;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObsClientDataConfig {

  @Bean
  ObsClientDataService obsClientDataService() {
    return new ObsClientDataServiceImpl();
  }
  
}
