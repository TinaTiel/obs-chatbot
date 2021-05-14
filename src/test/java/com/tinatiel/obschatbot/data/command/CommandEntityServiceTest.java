package com.tinatiel.obschatbot.data.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tinatiel.obschatbot.data.CommonConfig;
import com.tinatiel.obschatbot.data.command.entity.CommandEntity;
import com.tinatiel.obschatbot.data.command.entity.CommandEntityRepository;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import com.tinatiel.obschatbot.data.error.DataPersistenceException;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {CommonConfig.class, CommandDataConfig.class})
@DataJpaTest
public class CommandEntityServiceTest {

  @Autowired
  CommandEntityService service;

  @Autowired
  CommandEntityRepository repository;

  CommandEntity existingCommand;
  CommandEntity existingCommand2;

  int expectedIntialCount = 2;

  @BeforeEach
  void setUp() {
    repository.deleteAll();
    CommandEntity commandOnly = new CommandEntity();
    commandOnly.setName("toplevel");
    commandOnly.setDisabled(false);
    CommandEntity commandOnly2 = new CommandEntity();
    commandOnly2.setName("toplevel2");
    commandOnly2.setDisabled(false);

    existingCommand = repository.saveAndFlush(commandOnly);
    existingCommand2 = repository.saveAndFlush(commandOnly2);
  }

  @Test
  void createAndRetrieveNewCommand() {

    // Given a command
    CommandDto request = CommandDto.builder()
      .name("foo")
      .build();

    // When saved
    CommandDto result = service.save(request);

    // Then it can be retrieved
    assertThat(result.getId()).isNotNull();
    Optional<CommandDto> byId = service.findById(result.getId());
    Optional<CommandDto> byName = service.findByName(result.getName());

    assertThat(byId).isPresent();
    assertThat(byName).isPresent();
    assertThat(result).usingRecursiveComparison().isEqualTo(byName.get()).isEqualTo(byId.get());
    assertThat(result.getName()).isEqualTo(request.getName());
    assertThat(result.isDisabled()).isFalse();

  }

  @Test
  void listAll() {

    // Given some known number of commands exist
    assertThat(service.findAll()).hasSize(expectedIntialCount);

    // When a new command is saved
    service.save(CommandDto.builder()
      .name("foo")
      .build());

    // Then one more command can be retrieved
    assertThat(service.findAll()).hasSize(expectedIntialCount + 1);

  }

  @Test
  void nameNotProvided() {

    assertThatThrownBy(() -> {
      service.save(CommandDto.builder()
        .build());
    }).isInstanceOf(DataPersistenceException.class);

    assertThatThrownBy(() -> {
      service.save(CommandDto.builder()
        .name("  ")
        .build());
    }).isInstanceOf(DataPersistenceException.class);

  }

  @Test
  void nameAlreadyExists() {

    // Creating a new command on an existing name throws an exception
    assertThatThrownBy(() -> {
      service.save(CommandDto.builder()
        .name(existingCommand.getName())
        .build());
    }).isInstanceOf(DataPersistenceException.class);

    // Updating an existing command to the existing name
    assertThatThrownBy(() -> {
      service.save(CommandDto.builder()
        .id(existingCommand2.getId())
        .name(existingCommand.getName())
        .build());
    }).isInstanceOf(DataPersistenceException.class);

  }

  //  @Test
//  void disableCommand() {
//
//    // Given a request including the id
//  }

//  @Test
//  void renameCommand() {
//
//  }

}
