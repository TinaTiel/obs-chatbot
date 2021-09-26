package com.tinatiel.obschatbot.data.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.tinatiel.obschatbot.core.action.model.ExecuteCommandAction;
import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.sequencer.InOrderActionSequencer;
import com.tinatiel.obschatbot.data.command.entity.CommandEntityRepository;
import com.tinatiel.obschatbot.data.command.entity.sequencer.SequencerRepository;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import com.tinatiel.obschatbot.data.command.entity.action.ActionRepository;
import com.tinatiel.obschatbot.data.command.model.action.ExecuteCommandActionDto;
import com.tinatiel.obschatbot.data.command.model.action.ObsSourceVisibilityActionDto;
import com.tinatiel.obschatbot.data.command.model.action.SendMessageActionDto;
import com.tinatiel.obschatbot.data.command.model.action.WaitActionDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.InOrderSequencerDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import com.tinatiel.obschatbot.security.owner.SystemOwnerService;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CommandDataServicesIT {

  @Autowired
  CommandEntityService commandEntityService;
  @Autowired
  CommandService commandService;
  @Autowired
  CommandEntityRepository commandEntityRepository;
  @Autowired
  SequencerRepository sequencerRepository;
  @Autowired
  ActionRepository actionRepository;

  @Autowired
  OwnerService ownerService;

  UUID owner = UUID.randomUUID();

  @BeforeEach
  void beforeEach() {
    commandEntityRepository.findAll().forEach(it -> {
      System.out.println("Cleaning up command " + it.getId() + "('" + it.getName() + "')");
      commandEntityService.delete(it.getId());
    });
  }

  @Test
  void saveRetrieveUpdateRetrieveDelete() {

    long initialCommandCount = commandEntityRepository.count();
    long initialActionCount = actionRepository.count();
    long initialSequencerCount = sequencerRepository.count();

    // Given a command
    CommandDto request = CommandDto.builder()
      .id(UUID.randomUUID())
      .owner(owner)
      .name("somecommandwitheverything")
      .sequencer(InOrderSequencerDto.builder().id(UUID.randomUUID()).build())
      .actions(Arrays.asList(
        ObsSourceVisibilityActionDto.builder().id(UUID.randomUUID()).position(2).sourceName("donate").visible(true).build(),
        WaitActionDto.builder().id(UUID.randomUUID()).position(3).waitDuration(Duration.ofSeconds(2)).build(),
        ObsSourceVisibilityActionDto.builder().id(UUID.randomUUID()).position(4).sourceName("donate").visible(false).build()
      ))
      .build();

    // When saved
    CommandDto result = commandEntityService.save(request);

    // Then it can be retrieved
    CommandDto found = commandEntityService.findById(result.getId()).get();

    // And when updated
    CommandDto updateRequest = found;
    updateRequest.setActions(Arrays.asList(
      SendMessageActionDto.builder().id(UUID.randomUUID()).position(1).message("donate!").build(),
      result.getActions().get(0),
      result.getActions().get(1),
      result.getActions().get(2)
    ));

    CommandDto updateResult = commandEntityService.save(updateRequest);

    // Then it can be retrieved again
    CommandDto foundAgain = commandEntityService.findById(updateResult.getId()).get();
    assertThat(foundAgain).isNotNull();

    // And finally deleted
    commandEntityService.delete(foundAgain.getId());
    assertThat(commandEntityRepository.count()).isEqualTo(initialCommandCount);
    assertThat(actionRepository.count()).isEqualTo(initialActionCount);
    assertThat(sequencerRepository.count()).isEqualTo(initialSequencerCount);

  }

  @Test
  void retrieveExecutableCommand() {

    // Given we are using the System implementation of the Owner Service
    assertThat(ownerService).isInstanceOf(SystemOwnerService.class);

    // Given we save a command owned by the system
    CommandDto innerCommandDto = commandEntityService.save(CommandDto.builder()
      .id(UUID.randomUUID())
      .owner(SystemOwnerService.SYSTEM_ID)
      .name("inner")
      .sequencer(InOrderSequencerDto.builder().id(UUID.randomUUID()).build())
      .actions(Arrays.asList(
        SendMessageActionDto.builder().id(UUID.randomUUID()).position(1).message("world").build()
      ))
      .build());

    // And given we save another command that references that command
    CommandDto outerCommandDto = commandEntityService.save(CommandDto.builder()
      .id(UUID.randomUUID())
      .owner(SystemOwnerService.SYSTEM_ID)
      .name("outer")
      .sequencer(InOrderSequencerDto.builder().id(UUID.randomUUID()).build())
      .actions(Arrays.asList(
        SendMessageActionDto.builder().id(UUID.randomUUID()).position(1).message("hello").build(),
        ExecuteCommandActionDto.builder().id(UUID.randomUUID()).position(2).target(innerCommandDto.getId()).build()
      ))
    .build());

    // When we retrieve the executable command
    Optional<Command> actual = commandService.findByName(outerCommandDto.getName());

    // Then it is found
    assertThat(actual).isPresent();

    // And it matches what we expect
    InOrderActionSequencer innerCommandSequencer = new InOrderActionSequencer(false);
    innerCommandSequencer.setActions(Arrays.asList(
      new SendMessageAction("world")
    ));
    Command innerCommand = new Command().name("inner").actionSequencer(innerCommandSequencer);

    InOrderActionSequencer outerCommandSequencer = new InOrderActionSequencer(false);
    outerCommandSequencer.setActions(Arrays.asList(
      new SendMessageAction("hello"),
      new ExecuteCommandAction(innerCommand)
    ));
    Command expected = new Command().name("outer").actionSequencer(outerCommandSequencer);

    assertThat(actual.get()).usingRecursiveComparison().isEqualTo(expected);

  }
}
