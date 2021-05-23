package com.tinatiel.obschatbot.data.command.mapper.executable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.model.ExecuteCommandAction;
import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.error.CyclicalActionsException;
import com.tinatiel.obschatbot.core.sequencer.InOrderActionSequencer;
import com.tinatiel.obschatbot.data.DataConfig;
import com.tinatiel.obschatbot.data.command.CommandDataConfig;
import com.tinatiel.obschatbot.data.command.CommandEntityService;
import com.tinatiel.obschatbot.data.command.entity.CommandEntityRepository;
import com.tinatiel.obschatbot.data.command.entity.sequencer.SequencerRepository;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import com.tinatiel.obschatbot.data.command.model.action.ActionRepository;
import com.tinatiel.obschatbot.data.command.model.action.ExecuteCommandActionDto;
import com.tinatiel.obschatbot.data.command.model.action.SendMessageActionDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.InOrderSequencerDto;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.criteria.CriteriaBuilder.In;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = {CommandDataConfig.class})
@SpringJUnitConfig
public class ExecutableCommandMapperTest {

  @MockBean
  ActionRepository actionRepository;
  @MockBean
  SequencerRepository sequencerRepository;
  @MockBean
  CommandEntityRepository commandEntityRepository;

  @MockBean
  CommandEntityService commandService;

  @Autowired
  ExecutableCommandMapper mapper;

  @Test
  void mapNestedCommands() {

    // Given commands
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    UUID id3 = UUID.randomUUID();
    CommandDto commandDto1 = CommandDto.builder()
      .id(id1)
      .name("command1")
      .sequencer(InOrderSequencerDto.builder().build())
      .actions(Arrays.asList(
        SendMessageActionDto.builder().message("foo").build(),
        ExecuteCommandActionDto.builder().target(id2).build(),
        SendMessageActionDto.builder().message("bar").build()
        ))
      .build();
    CommandDto commandDto2 = CommandDto.builder()
      .id(id2)
      .name("command2")
      .sequencer(InOrderSequencerDto.builder().build())
      .actions(Arrays.asList(
        SendMessageActionDto.builder().message("whoo").build(),
        ExecuteCommandActionDto.builder().target(id3).build(),
        SendMessageActionDto.builder().message("hoo").build()
      ))
      .build();
    CommandDto commandDto3 = CommandDto.builder()
      .id(id3)
      .name("command3")
      .actions(Arrays.asList(
        SendMessageActionDto.builder().message("mamma mia").build()
      ))
      .build();

    InOrderActionSequencer sequencer3 = new InOrderActionSequencer(false);
    sequencer3.setActions(Arrays.asList(
      new SendMessageAction("mamma mia")
    ));
    Command command3 = new Command()
      .name("command3")
      .actionSequencer(sequencer3);

    InOrderActionSequencer sequencer2 = new InOrderActionSequencer(false);
    sequencer2.setActions(Arrays.asList(
      new SendMessageAction("whoo"),
      new ExecuteCommandAction(command3),
      new SendMessageAction("hoo")
    ));
    Command command2 = new Command()
      .name("command2")
      .actionSequencer(sequencer2);

    InOrderActionSequencer sequencer1 = new InOrderActionSequencer(false);
    sequencer1.setActions(Arrays.asList(
      new SendMessageAction("foo"),
      new ExecuteCommandAction(command2),
      new SendMessageAction("bar")
    ));
    Command command1 = new Command()
      .name(commandDto1.getName())
      .actionSequencer(sequencer1);

    // And given service finds those commands
    when(commandService.findById(id1)).thenReturn(Optional.of(commandDto1));
    when(commandService.findById(id2)).thenReturn(Optional.of(commandDto2));
    when(commandService.findById(id3)).thenReturn(Optional.of(commandDto3));

    // When mapped
    Command result1 = mapper.map(commandDto1);

    // Then it matches as expected
    assertThat(result1).usingRecursiveComparison().isEqualTo(command1);
    List<Action> result1Actions = result1.getActions();
    List<Action> expected1Actions = command1.getActions();
    assertThat(result1Actions).usingRecursiveComparison().isEqualTo(expected1Actions);

    Command result2 = ((ExecuteCommandAction)result1Actions.get(1)).getTarget();
    assertThat(result2).usingRecursiveComparison().isEqualTo(command2);
    List<Action> result2Actions = result2.getActions();
    List<Action> expected2Actions = command2.getActions();
    assertThat(result2Actions).usingRecursiveComparison().isEqualTo(expected2Actions);

    Command result3 = ((ExecuteCommandAction)result2Actions.get(1)).getTarget();
    assertThat(result3).usingRecursiveComparison().isEqualTo(command3);
    List<Action> result3Actions = result3.getActions();
    List<Action> expected3Actions = command3.getActions();
    assertThat(result3Actions).usingRecursiveComparison().isEqualTo(expected3Actions);

  }

  @Test
  void mapCommandWithCycles() {

    // Given commands
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    UUID id3 = UUID.randomUUID();
    CommandDto commandDto1 = CommandDto.builder()
      .id(id1)
      .name("command1")
      .sequencer(InOrderSequencerDto.builder().build())
      .actions(Arrays.asList(
        SendMessageActionDto.builder().message("foo").build(),
        ExecuteCommandActionDto.builder().target(id2).build(),
        SendMessageActionDto.builder().message("bar").build()
      ))
      .build();
    CommandDto commandDto2 = CommandDto.builder()
      .id(id2)
      .name("command2")
      .sequencer(InOrderSequencerDto.builder().build())
      .actions(Arrays.asList(
        SendMessageActionDto.builder().message("whoo").build(),
        ExecuteCommandActionDto.builder().target(id3).build(),
        SendMessageActionDto.builder().message("hoo").build()
      ))
      .build();
    CommandDto commandDto3 = CommandDto.builder()
      .id(id3)
      .name("command3")
      .actions(Arrays.asList(
        ExecuteCommandActionDto.builder().target(id1).build() // Here is the recursion
      ))
      .build();

    // And given service finds those commands
    when(commandService.findById(id1)).thenReturn(Optional.of(commandDto1));
    when(commandService.findById(id2)).thenReturn(Optional.of(commandDto2));
    when(commandService.findById(id3)).thenReturn(Optional.of(commandDto3));

    // When mapped, then an exception is thrown
    assertThatThrownBy(() -> {
      mapper.map(commandDto1);
    }).isInstanceOf(CyclicalActionsException.class);

  }

}
