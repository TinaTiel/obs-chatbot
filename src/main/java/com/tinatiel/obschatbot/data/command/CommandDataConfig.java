package com.tinatiel.obschatbot.data.command;

import com.tinatiel.obschatbot.data.command.entity.CommandEntityRepository;
import com.tinatiel.obschatbot.data.command.entity.sequencer.SequencerRepository;
import com.tinatiel.obschatbot.data.command.mapper.CommandMapper;
import com.tinatiel.obschatbot.data.command.mapper.executable.ExecutableActionMapper;
import com.tinatiel.obschatbot.data.command.mapper.executable.ExecutableActionMapperImpl;
import com.tinatiel.obschatbot.data.command.mapper.executable.ExecutableCommandMapper;
import com.tinatiel.obschatbot.data.command.mapper.executable.ExecutableCommandMapperImpl;
import com.tinatiel.obschatbot.data.command.mapper.executable.ExecutableSequencerMapper;
import com.tinatiel.obschatbot.data.command.mapper.executable.ExecutableSequencerMapperImpl;
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

  @Autowired
  SequencerRepository sequencerRepository;

  @Bean
  CommandMapper commandMapper() {
    return Mappers.getMapper(CommandMapper.class);
  }

  @Bean
  ExecutableCommandMapper executableCommandMapper() {
    return new ExecutableCommandMapperImpl(
      executableSequencerMapper(),
      executableActionMapper(),
      commandEntityService()
    );
  }

  @Bean
  ExecutableActionMapper executableActionMapper() {
    return new ExecutableActionMapperImpl();
  }

  @Bean
  ExecutableSequencerMapper executableSequencerMapper() {
    return new ExecutableSequencerMapperImpl();
  }

  @Bean
  CommandEntityService commandEntityService() {
    return new CommandEntityServiceImpl(commandEntityRepository, sequencerRepository, commandMapper());
  }

  @Bean
  public CommandService commandRepository() {
    return new CommandServiceImpl(commandEntityService(), executableCommandMapper());
  }

}
