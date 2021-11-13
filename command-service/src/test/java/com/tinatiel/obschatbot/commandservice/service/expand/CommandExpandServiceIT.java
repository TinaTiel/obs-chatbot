package com.tinatiel.obschatbot.commandservice.service.expand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.commandservice.dto.CommandDto;
import com.tinatiel.obschatbot.commandservice.dto.action.ExecuteSequenceAction;
import com.tinatiel.obschatbot.commandservice.dto.action.TestAction;
import com.tinatiel.obschatbot.commandservice.dto.action.actionsequence.TestActionSequence;
import com.tinatiel.obschatbot.commandservice.service.CommandService;
import com.tinatiel.obschatbot.commandservice.service.CyclicalReferenceException;
import com.tinatiel.obschatbot.commandservice.service.expand.CommandExpandService;
import com.tinatiel.obschatbot.commandservice.service.expand.CommandExpandServiceConfig;
import com.tinatiel.obschatbot.commandservice.service.expand.CommandExpandServiceImpl;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import reactor.core.publisher.Mono;

@ContextConfiguration(classes = {CommandExpandServiceConfig.class})
@SpringJUnitConfig
public class CommandExpandServiceIT {

  @Autowired
  CommandExpandService commandExpandService;

  @MockBean
  CommandService commandService;

  @Test
  void shouldDereferenceExecuteSequenceActionsRecursively() {

    // Given a heirarchy of actions
    TestAction action01 = new TestAction();
    ExecuteSequenceAction action02 = ExecuteSequenceAction.builder().commandId(UUID.randomUUID()).build();
      TestAction action021 = new TestAction();
      TestAction action022 = new TestAction();
      ExecuteSequenceAction action023 = ExecuteSequenceAction.builder().commandId(UUID.randomUUID()).build();
        TestAction action0231 = new TestAction();
        TestAction action0232 = new TestAction();
        TestAction action0233 = new TestAction();
      ExecuteSequenceAction action024 = ExecuteSequenceAction.builder().commandId(UUID.randomUUID()).build();
        TestAction action0241 = new TestAction();
        TestAction action0242 = new TestAction();
    TestAction action03 = new TestAction();
    ExecuteSequenceAction action04 = ExecuteSequenceAction.builder().commandId(UUID.randomUUID()).build();
      TestAction action041 = new TestAction();
      TestAction action042 = new TestAction();

    // And given commands that express that hierarchy
    CommandDto commandForRoot = CommandDto.builder()
      .id(UUID.randomUUID())
      .name("root")
      .actionSequence(new TestActionSequence(List.of(
        action01, action02, action03, action04
      )))
      .build();

    CommandDto commandForAction02 = CommandDto.builder()
      .id(action02.getCommandId())
      .name("commandForAction02")
      .actionSequence(new TestActionSequence(List.of(
        action021, action022, action023, action024
      )))
      .build();
    CommandDto commandForAction04 = CommandDto.builder()
      .id(action04.getCommandId())
      .name("commandForAction04")
      .actionSequence(new TestActionSequence(List.of(
        action041, action042
      )))
      .build();

    CommandDto commandForAction023 = CommandDto.builder()
      .id(action023.getCommandId())
      .name("commandForAction023")
      .actionSequence(new TestActionSequence(List.of(
        action0231, action0232, action0233
      )))
      .build();
    CommandDto commandForAction024 = CommandDto.builder()
      .id(action024.getCommandId())
      .name("commandForAction024")
      .actionSequence(new TestActionSequence(List.of(
        action0241, action0242
      )))
      .build();

    // And given the commands are found
    when(commandService.findById(commandForRoot.getId()))
      .thenReturn(Mono.just(commandForRoot));
    when(commandService.findById(commandForAction02.getId()))
      .thenReturn(Mono.just(commandForAction02));
    when(commandService.findById(commandForAction04.getId()))
      .thenReturn(Mono.just(commandForAction04));
    when(commandService.findById(commandForAction023.getId()))
      .thenReturn(Mono.just(commandForAction023));
    when(commandService.findById(commandForAction024.getId()))
      .thenReturn(Mono.just(commandForAction024));

    // When expanded
    CommandDto result = commandExpandService.expand(commandForRoot).block();

    // Then it matches as expected
    CommandDto expected = commandForRoot
      .withActionSequence(new TestActionSequence(List.of(
        action01,
        action02.withActionSequence(new TestActionSequence(List.of(
          action021,
          action022,
          action023.withActionSequence(new TestActionSequence(List.of(
            action0231, action0232, action0233
          ))),
          action024.withActionSequence(new TestActionSequence(List.of(
            action0241, action0242
          )))
        ))),
        action03,
        action04.withActionSequence(new TestActionSequence(List.of(
          action041, action042
        )))
      )));
    assertThat(result).usingRecursiveComparison().isEqualTo(expected);

  }

  @Test
  void shouldRejectCyclicalReferences() {

    // Given a root command with an executeSequence action
    TestAction action1 = new TestAction();
    ExecuteSequenceAction action2 = ExecuteSequenceAction.builder().commandId(UUID.randomUUID()).build();
    TestAction action3 = new TestAction();
    CommandDto root = CommandDto.builder()
      .id(UUID.randomUUID())
      .actionSequence(new TestActionSequence(List.of(
        action1, action2, action3
      )))
      .build();

    // And given another command with also an ExecuteSequenceAction
    TestAction action21 = new TestAction();
    TestAction action22 = new TestAction();
    ExecuteSequenceAction action23 = ExecuteSequenceAction.builder().commandId(UUID.randomUUID()).build();
    TestAction action24 = new TestAction();
    CommandDto action2comm = CommandDto.builder()
      .id(UUID.randomUUID())
      .actionSequence(new TestActionSequence(List.of(
        action21, action22, action23, action24
      )))
      .build();

    // And given the commands are found, but one of them is cyclical
    when(commandService.findById(action2.getCommandId()))
      .thenReturn(Mono.just(action2comm));
    when(commandService.findById(action23.getCommandId()))
      .thenReturn(Mono.just(root));

    // When dereferenced
    // Then an exception is thrown
    assertThatThrownBy(() -> {
      commandExpandService.expand(root).block();
    }).isInstanceOf(CyclicalReferenceException.class);

  }

}
