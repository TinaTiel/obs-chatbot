package com.tinatiel.obschatbot.data.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tinatiel.obschatbot.data.CommonConfig;
import com.tinatiel.obschatbot.data.command.entity.CommandEntity;
import com.tinatiel.obschatbot.data.command.entity.CommandEntityRepository;
import com.tinatiel.obschatbot.data.command.entity.sequencer.SequencerEntity;
import com.tinatiel.obschatbot.data.command.entity.sequencer.SequencerEntity.Type;
import com.tinatiel.obschatbot.data.command.entity.sequencer.SequencerRepository;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.InOrderSequencerDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.RandomOrderSequencerDto;
import com.tinatiel.obschatbot.data.error.DataPersistenceException;
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
  @Autowired
  SequencerRepository sequencerRepository;

  CommandEntity existingCommand;
  CommandEntity existingCommand2;
  CommandEntity existingCommandWithSequencer;

  int expectedIntialCount;

  @BeforeEach
  void setUp() {

      // cleanup data
      sequencerRepository.deleteAll();
      repository.deleteAll();

      // create commands with no children
      CommandEntity commandOnly = new CommandEntity();
      commandOnly.setName("toplevel");
      commandOnly.setDisabled(false);
      CommandEntity commandOnly2 = new CommandEntity();
      commandOnly2.setName("toplevel2");
      commandOnly2.setDisabled(false);

      // create commands with sequencer
      SequencerEntity seq1 = new SequencerEntity();
      seq1.setSequencerType(Type.ORDERED);
      seq1.setReversed(false);
      CommandEntity commandWithSeq1 = new CommandEntity();
      commandWithSeq1.setName("inorder");
      commandWithSeq1.setSequencer(seq1);
      seq1.setCommand(commandWithSeq1);

      // save commands
      existingCommand = repository.saveAndFlush(commandOnly);
      existingCommand2 = repository.saveAndFlush(commandOnly2);
      existingCommandWithSequencer = repository.saveAndFlush(commandWithSeq1);
      sequencerRepository.flush();

      // update the total
      expectedIntialCount = 3;

      System.out.println("FINISHED INITIALIZING TEST DATA");
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

    @Test
  void disableCommand() {

      // Given a command is not disabled
      assertThat(existingCommand.isDisabled()).isFalse();

      // Given a request to disable it
      CommandDto disableRequest = CommandDto.builder()
        .id(existingCommand.getId())
        .name(existingCommand.getName())
        .disabled(true)
        .build();

      // When updated
      service.save(disableRequest);

      // Then it is now disabled
      assertThat(service.findById(disableRequest.getId()).get().isDisabled()).isTrue();

  }

  @Test
  void renameCommand() {

    // Given a request to rename an existing command
    CommandDto disableRequest = CommandDto.builder()
      .id(existingCommand.getId())
      .name("newname")
      .disabled(existingCommand.isDisabled())
      .build();

    // When updated
    service.save(disableRequest);

    // Then it is renamed
    assertThat(service.findById(disableRequest.getId()).get().getName()).isEqualTo("newname");

    // And there are no duplicates
    assertThat(repository.findAll()).hasSize(expectedIntialCount);

  }

  @Test
  public void createCommandWithSequencer() {

    // Given a command with a sequencer
    CommandDto request = CommandDto.builder()
      .name("sequenced")
      .sequencer(InOrderSequencerDto.builder().reversed(false).build())
      .build();

    // When saved and retrieved
    CommandDto result = service.save(request);

    // Then it matches as expected
    CommandDto found = service.findById(result.getId()).get(); // will throw it not found, fine :)
    System.out.println(found);
    assertThat(found).usingRecursiveComparison().isEqualTo(result);
    assertThat(found.getSequencer())
      .isNotNull()
      .usingRecursiveComparison().isEqualTo(request.getSequencer());

  }

  @Test
  void changeCommandSequencer() {

    // Given an initial count of sequencer entities
    long intialSeqCount = sequencerRepository.count();

    // Given a request to update the sequencer of an existing command
    CommandDto request = CommandDto.builder()
      .id(existingCommandWithSequencer.getId())
      .name(existingCommandWithSequencer.getName())
      .sequencer(RandomOrderSequencerDto.builder()
        .pickedPerExecution(69)
        .build()
      )
      .build();

    // When saved
    CommandDto result = service.save(request);

    // Then it was updated as expected
    CommandDto found = service.findById(existingCommandWithSequencer.getId()).get();
    assertThat(found).isNotNull().usingRecursiveComparison().isEqualTo(result);
    assertThat(found.getSequencer()).usingRecursiveComparison().isEqualTo(request.getSequencer());

    // And there are no duplicates
    assertThat(sequencerRepository.count()).isEqualTo(intialSeqCount);

  }

}
