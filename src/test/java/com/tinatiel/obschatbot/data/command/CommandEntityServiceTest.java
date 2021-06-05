package com.tinatiel.obschatbot.data.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;

import com.tinatiel.obschatbot.data.common.CommonConfig;
import com.tinatiel.obschatbot.data.command.entity.CommandEntity;
import com.tinatiel.obschatbot.data.command.entity.CommandEntityRepository;
import com.tinatiel.obschatbot.data.command.entity.action.ObsSourceVisibilityActionEntity;
import com.tinatiel.obschatbot.data.command.entity.action.WaitActionEntity;
import com.tinatiel.obschatbot.data.command.entity.sequencer.InOrderSequencerEntity;
import com.tinatiel.obschatbot.data.command.entity.sequencer.SequencerRepository;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import com.tinatiel.obschatbot.data.command.model.action.ActionDto;
import com.tinatiel.obschatbot.data.command.entity.action.ActionRepository;
import com.tinatiel.obschatbot.data.command.model.action.ObsSourceVisibilityActionDto;
import com.tinatiel.obschatbot.data.command.model.action.SendMessageActionDto;
import com.tinatiel.obschatbot.data.command.model.action.WaitActionDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.InOrderSequencerDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.RandomOrderSequencerDto;
import com.tinatiel.obschatbot.data.error.DataPersistenceException;
import com.tinatiel.obschatbot.data.owner.OwnerConfig;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
  CommonConfig.class,
  CommandDataConfig.class,
  OwnerConfig.class,
  CommandEntityServiceTest.TestConfig.class})
@DataJpaTest
public class CommandEntityServiceTest {

  @Configuration
  @EnableJpaRepositories(basePackages = "com.tinatiel.obschatbot.data.command.entity")
  static class TestConfig { }

  @Autowired
  CommandEntityService service;

  @Autowired
  CommandEntityRepository commandRepository;
  @Autowired
  SequencerRepository sequencerRepository;
  @Autowired
  ActionRepository actionRepository;

  UUID owner = UUID.randomUUID();
  CommandEntity existingCommand;
  CommandEntity existingCommand2;
  CommandEntity existingCommandWithSequencer;
  CommandEntity existingCommandWithActions;

  int initialCommandCount;
  int initialActionCount;

  @BeforeEach
  void setUp() {

      // cleanup data
      sequencerRepository.deleteAll();
      actionRepository.deleteAll();
      commandRepository.deleteAll();
      assertThat(sequencerRepository.count()).isZero();
      assertThat(actionRepository.count()).isZero();
      assertThat(commandRepository.count()).isZero();

      // create commands with no children
      CommandEntity commandOnly = new CommandEntity();
      commandOnly.setOwner(owner);
      commandOnly.setName("toplevel");
      commandOnly.setDisabled(false);

      CommandEntity commandOnly2 = new CommandEntity();
      commandOnly2.setOwner(owner);
      commandOnly2.setName("toplevel2");
      commandOnly2.setDisabled(false);

      // create commands with sequencer
      InOrderSequencerEntity seq1 = new InOrderSequencerEntity();
      seq1.setReversed(false);
      CommandEntity commandWithSeq1 = new CommandEntity();
      commandWithSeq1.setOwner(owner);
      commandWithSeq1.setName("inorder");
      commandWithSeq1.setSequencer(seq1);

      // create command with actions
      ObsSourceVisibilityActionEntity action1 = new ObsSourceVisibilityActionEntity();
      action1.setPosition(1);
      action1.setSourceName("meme");
      action1.setVisible(true);
      WaitActionEntity action2 = new WaitActionEntity();
      action2.setPosition(2);
      action2.setWaitDuration(Duration.ofSeconds(2));
      ObsSourceVisibilityActionEntity action3 = new ObsSourceVisibilityActionEntity();
      action3.setPosition(3);
      action3.setSourceName("meme");
      action3.setVisible(false);
      CommandEntity commandWithActions = new CommandEntity();
      commandWithActions.setOwner(owner);
      commandWithActions.setName("twoactions");
      commandWithActions.setActions(Arrays.asList(action1, action2, action3));

      // save commands
      existingCommand = commandRepository.saveAndFlush(commandOnly);
      existingCommand2 = commandRepository.saveAndFlush(commandOnly2);
      existingCommandWithSequencer = commandRepository.saveAndFlush(commandWithSeq1);
      existingCommandWithActions = commandRepository.saveAndFlush(commandWithActions);
      sequencerRepository.flush();

      // update the total
      initialCommandCount = 4;
      initialActionCount = 3;

      assertThat(commandRepository.count()).isEqualTo(initialCommandCount);
      assertThat(actionRepository.count()).isEqualTo(initialActionCount);

      System.out.println("FINISHED INITIALIZING TEST DATA");
  }

  @Test
  void createAndRetrieveNewCommand() {

      // Given a command
      CommandDto request = CommandDto.builder()
        .owner(UUID.randomUUID())
        .name("foo")
        .build();

      // When saved
      CommandDto result = service.save(request);

      // Then it can be retrieved
      assertThat(result.getId()).isNotNull();
      Optional<CommandDto> byId = service.findById(result.getId());
      Optional<CommandDto> byName = service.findByNameAndOwner(request.getName(), request.getOwner());

      assertThat(byId).isPresent();
      assertThat(byName).isPresent();
      assertThat(result).usingRecursiveComparison().isEqualTo(byName.get()).isEqualTo(byId.get());
      assertThat(result.getName()).isEqualTo(request.getName());
      assertThat(result.isDisabled()).isFalse();

  }

  @Test
  void findByNameAndOwner() {

    assertThat(service.findByNameAndOwner(existingCommand.getName(), null)).isEmpty();
    assertThat(service.findByNameAndOwner(null, existingCommand.getOwner())).isEmpty();
    assertThat(service.findByNameAndOwner(existingCommand.getName(), existingCommand.getOwner())).isPresent();

  }

  @Test
  void listAllByOwner() {

      // Given some known number of commands exist
      assertThat(service.findByOwner(owner)).hasSize(initialCommandCount);

      // When a new command for same owner is saved
      service.save(CommandDto.builder()
        .owner(owner)
        .name("foo")
        .build());

      // And a new command for a different owner is saved with the same name
      UUID otherOwner = UUID.randomUUID();
      service.save(CommandDto.builder()
        .owner(otherOwner)
        .name("foo")
        .build());

      // Then one more command can be retrieved for the existing owner
      assertThat(service.findByOwner(owner)).hasSize(initialCommandCount + 1);

      // And the created command for the other owner
      assertThat(service.findByOwner(otherOwner)).hasSize(1);

  }

  @Test
  void nameNotProvided() {

      assertThatThrownBy(() -> {
        service.save(CommandDto.builder()
          .owner(owner)
          .build());
      }).isInstanceOf(DataPersistenceException.class);

      assertThatThrownBy(() -> {
        service.save(CommandDto.builder()
          .owner(owner)
          .name("  ")
          .build());
      }).isInstanceOf(DataPersistenceException.class);

  }

  @Test
  void ownerNotProvided() {

    assertThatThrownBy(() -> {
      service.save(CommandDto.builder()
        .name("foo")
        .build());
    }).isInstanceOf(DataPersistenceException.class);

  }

  @Test
  void nameAlreadyExistsForOwner() {

      // Creating a new command on an existing name throws an exception
      assertThatThrownBy(() -> {
        service.save(CommandDto.builder()
          .owner(owner)
          .name(existingCommand.getName())
          .build());
      }).isInstanceOf(DataPersistenceException.class);

      // Updating an existing command to the existing name
      assertThatThrownBy(() -> {
        service.save(CommandDto.builder()
          .owner(owner)
          .id(existingCommand2.getId())
          .name(existingCommand.getName())
          .build());
      }).isInstanceOf(DataPersistenceException.class);

  }

  @Test
  void sameNameDifferentOwnerIsOkay() {

    // Create a new command, same name as existing but with different owner throws no exceptions
    UUID otherOwner = UUID.randomUUID();
    service.save(CommandDto.builder()
      .owner(otherOwner)
      .name(existingCommand.getName())
      .build());

  }

  @Test
  void disableCommand() {

      // Given a command is not disabled
      assertThat(existingCommand.isDisabled()).isFalse();

      // Given a request to disable it
      CommandDto disableRequest = CommandDto.builder()
        .owner(owner)
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
      .owner(owner)
      .id(existingCommand.getId())
      .name("newname")
      .disabled(existingCommand.isDisabled())
      .build();

    // When updated
    service.save(disableRequest);

    // Then it is renamed
    assertThat(service.findById(disableRequest.getId()).get().getName()).isEqualTo("newname");

    // And there are no duplicates
    assertThat(commandRepository.findAll()).hasSize(initialCommandCount);

  }

  @Test
  public void createCommandWithSequencer() {

    // Given a command with a sequencer
    CommandDto request = CommandDto.builder()
      .owner(UUID.randomUUID())
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
      .owner(owner)
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

  @Test
  void createCommandWithActions() {

    // Given a command with actions
    CommandDto request = CommandDto.builder()
      .owner(UUID.randomUUID())
      .name("withactions")
      .actions(Arrays.asList(
          SendMessageActionDto.builder().position(1).message("donate!").build(),
          ObsSourceVisibilityActionDto.builder().position(2).sourceName("donate").visible(true).build(),
          WaitActionDto.builder().position(3).waitDuration(Duration.ofSeconds(2)).build(),
          ObsSourceVisibilityActionDto.builder().position(4).sourceName("donate").visible(false).build()
        ))
      .build();

    // When saved
    CommandDto result = service.save(request);

    // Then it can be retrieved with those actions
    CommandDto found = service.findById(result.getId()).get();
    System.out.println(found.getActions());
    assertThat(found).isNotNull().usingRecursiveComparison().isEqualTo(result);
    assertThat(found.getActions())
      .usingFieldByFieldElementComparator()
      .usingRecursiveComparison()
      .isEqualTo(result.getActions());

  }

  @Test
  void reduceCommandActions() {

    // Given an existing command with actions
    CommandDto existing = service.findById(existingCommandWithActions.getId())
      .orElseThrow(() -> new AssertionError("command doesn't exist as expected"));

    // And given a request to reduce those actions
    CommandDto request = CommandDto.builder()
      .owner(owner)
      .id(existing.getId())
      .name(existing.getName())
      .actions(Arrays.asList(
        existing.getActions().get(1)
      ))
      .build();

    // When saved
    CommandDto result = service.save(request);

    // Then there are now fewer actions
    assertThat(actionRepository.count()).isEqualTo(1);

    // And it has the expected actions
    CommandDto found = service.findById(result.getId()).get();
    System.out.println(found.getActions());
    assertThat(found).isNotNull().usingRecursiveComparison().isEqualTo(result);
    assertThat(found.getActions())
      .hasSameSizeAs(request.getActions())
      .usingFieldByFieldElementComparator()
      .usingRecursiveComparison()
      .isEqualTo(result.getActions());

  }

  @Test
  void insertCommandActions() {
    // Given an existing command with actions
    CommandDto existing = service.findById(existingCommandWithActions.getId())
      .orElseThrow(() -> new AssertionError("command doesn't exist as expected"));

    // And given a request to insert an additional action
    ActionDto action1 = existing.getActions().get(0);
    action1.setPosition(1);
    ActionDto action2 = SendMessageActionDto.builder()
      .message("Whoohoo!")
      .position(2)
      .build();
    ActionDto action3 = existing.getActions().get(1);
    action3.setPosition(2);
    ActionDto action4 = existing.getActions().get(2);
    action4.setPosition(3);
    CommandDto request = CommandDto.builder()
      .owner(owner)
      .id(existing.getId())
      .name(existing.getName())
      .actions(Arrays.asList(
        action1, action2, action3, action4
      ))
      .build();

    // When saved
    CommandDto result = service.save(request);

    // Then there are now more actions
    assertThat(actionRepository.count()).isEqualTo(initialActionCount+1);

    // And it has the expected actions
    CommandDto found = service.findById(result.getId()).get();
    System.out.println(found.getActions());
    assertThat(found).isNotNull().usingRecursiveComparison().isEqualTo(result);
    assertThat(found.getActions())
      .hasSameSizeAs(request.getActions())
      .usingFieldByFieldElementComparator()
      .usingRecursiveComparison()
      .isEqualTo(result.getActions());
  }

  @Test
  void reorderCommandActions() {

    // Given an existing command with actions
    CommandDto existing = service.findById(existingCommandWithActions.getId())
      .orElseThrow(() -> new AssertionError("command doesn't exist as expected"));

    // And given a request to reorder those actions
    ActionDto action1 = existing.getActions().get(1);
    action1.setPosition(1);
    ActionDto action2 = existing.getActions().get(0);
    action2.setPosition(2);
    ActionDto action3 = existing.getActions().get(2);
    action3.setPosition(3);

    CommandDto request = CommandDto.builder()
      .owner(owner)
      .id(existing.getId())
      .name(existing.getName())
      .actions(Arrays.asList(action1, action2, action3))
      .build();

    // When saved
    CommandDto result = service.save(request);

    // Then there are the same number of actions
    assertThat(actionRepository.count()).isEqualTo(initialActionCount);

    // But it has been reordered
    CommandDto found = service.findById(result.getId()).get();
    System.out.println(found.getActions());
    assertThat(found).isNotNull().usingRecursiveComparison().isEqualTo(result);
    assertThat(found.getActions())
      .hasSameSizeAs(request.getActions())
      .usingFieldByFieldElementComparator()
      .usingRecursiveComparison()
      .isEqualTo(result.getActions());
  }

  @Test
  void deleteCommandWithSequencer() {

    // When a command is deleted
    service.delete(existingCommandWithSequencer.getId());

    // Then it is deleted
    assertThat(commandRepository.count()).isEqualTo(initialCommandCount - 1);

    // Then there are no remaining children
    assertThat(sequencerRepository.count()).isZero();

  }

  @Test
  void deleteCommandWithActions() {

    // When a command is deleted
    service.delete(existingCommandWithActions.getId());

    // Then it is deleted
    assertThat(commandRepository.count()).isEqualTo(initialCommandCount - 1);

    // Then there are no remaining children
    assertThat(actionRepository.count()).isZero();

  }

  @Test
  void deleteCommandThatDoesntExist() {
    assertThatThrownBy(() -> {
      service.delete(UUID.randomUUID());
    }).isInstanceOf(DataPersistenceException.class);
  }
}
