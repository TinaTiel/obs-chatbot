package com.tinatiel.obschatbot.data.client.obs;

import com.tinatiel.obschatbot.data.client.obs.entity.ObsClientDataRepository;
import com.tinatiel.obschatbot.data.client.obs.mapper.ObsClientDataMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EntityScan("com.tinatiel.obschatbot.data.client.obs.entity")
@Configuration
public class ObsClientDataConfig {

  @Autowired
  ObsClientDataRepository obsClientDataRepository;

  @Bean
  ObsClientDataMapper obsClientDataMapper() {
    return Mappers.getMapper(ObsClientDataMapper.class);
  }

  @Bean
  ObsClientDataService obsClientDataService() {
    return new ObsClientDataServiceImpl(
      obsClientDataRepository,
      obsClientDataMapper()
    );
  }
  
}
