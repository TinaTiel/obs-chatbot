package com.tinatiel.obschatbot.data.command;

import com.tinatiel.obschatbot.data.command.entity.CommandEntityRepository;
import com.tinatiel.obschatbot.data.command.mapper.CommandMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure the command domain services and entities.
 */
@EntityScan("com.tinatiel.obschatbot.data.command.entity")
@Configuration
public class CommandDataConfig {

  @Autowired
  CommandEntityRepository commandEntityRepository;

  @Bean
  CommandMapper commandMapper() {
    return Mappers.getMapper(CommandMapper.class);
  }

  @Bean
  CommandEntityService commandEntityService() {
    return new CommandEntityServiceImpl(commandEntityRepository, commandMapper());
  }

}
