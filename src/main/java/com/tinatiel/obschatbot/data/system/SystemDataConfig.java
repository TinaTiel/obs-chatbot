package com.tinatiel.obschatbot.data.system;

import com.tinatiel.obschatbot.data.owner.OwnerService;
import com.tinatiel.obschatbot.data.owner.SystemOwnerService;
import com.tinatiel.obschatbot.data.system.entity.SystemSettingsRepository;
import com.tinatiel.obschatbot.data.system.mapper.SystemSettingsMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EntityScan("com.tinatiel.obschatbot.data.system.entity")
@Configuration
public class SystemDataConfig {

  @Autowired
  SystemSettingsRepository systemSettingsRepository;

  @Bean
  SystemSettingsMapper systemSettingsMapper() {
    return Mappers.getMapper(SystemSettingsMapper.class);
  }

  @Bean
  SystemSettingsService systemSettingsService() {
    return new SystemSettingsServiceImpl(systemSettingsRepository, systemSettingsMapper());
  }

}
