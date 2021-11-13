package com.tinatiel.obschatbot.commandservice.service.expand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.commandservice.dto.CommandDto;
import com.tinatiel.obschatbot.commandservice.dto.action.ExecuteSequenceAction;
import com.tinatiel.obschatbot.commandservice.dto.action.TestAction;
import com.tinatiel.obschatbot.commandservice.dto.action.actionsequence.TestActionSequence;
import com.tinatiel.obschatbot.commandservice.service.CyclicalReferenceException;
import com.tinatiel.obschatbot.commandservice.service.expand.CommandExpandService;
import com.tinatiel.obschatbot.commandservice.service.expand.CommandExpandServiceImpl;
import com.tinatiel.obschatbot.commandservice.service.expand.CommandExpander;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class CommandExpanderTest {

  CommandExpander sut;

  @BeforeEach
  void setUp() {
    sut = new CommandExpander();
  }

  @Test
  void shouldRecursivelyExpandCommand() {

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

    // And a map of command ids to commands
    Map<UUID, CommandDto> commandMap = Map.of(
      commandForRoot.getId(), commandForRoot,
      commandForAction02.getId(), commandForAction02,
      commandForAction04.getId(), commandForAction04,
      commandForAction023.getId(), commandForAction023,
      commandForAction024.getId(), commandForAction024
    );

    // When expanded
    CommandDto result = sut.expand(commandForRoot, commandMap);

    // Then the root includes the full sequences recursively
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
    // But with one command referring to one higher in the chain (e.g. root)
    TestAction action21 = new TestAction();
    TestAction action22 = new TestAction();
    ExecuteSequenceAction action23 = ExecuteSequenceAction.builder().commandId(root.getId()).build();
    TestAction action24 = new TestAction();
    CommandDto action2comm = CommandDto.builder()
      .id(action2.getCommandId())
      .actionSequence(new TestActionSequence(List.of(
        action21, action22, action23, action24
      )))
      .build();

    // And given a map of command ids to commands
    // (full list isn't necessary...we can exclude action23's command)
    Map<UUID, CommandDto> commandMap = Map.of(
      root.getId(), root,
      action2comm.getId(), action2comm
    );

    // When dereferenced
    // Then an exception is thrown
    assertThatThrownBy(() -> {
      sut.expand(root, commandMap);
    }).isInstanceOf(CyclicalReferenceException.class);

  }

  @Test
  void shouldReturnRootIfNoCommandsInMap() {

    // Given a root command no execute sequence actions
    CommandDto rootWithNoExecuteSequenceActions = CommandDto.builder()
      .id(UUID.randomUUID())
      .actionSequence(new TestActionSequence(List.of(
        new TestAction(),
        new TestAction(),
        new TestAction()
      )))
      .build();

    // And given a different root command but with sequence actions
    CommandDto rootWithExecuteSequenceActions = CommandDto.builder()
      .id(UUID.randomUUID())
      .actionSequence(new TestActionSequence(List.of(
        new TestAction(),
        ExecuteSequenceAction.builder().commandId(UUID.randomUUID()).build(),
        new TestAction()
      )))
      .build();

    // But given an empty map of command ids to commands
    Map<UUID, CommandDto> commandMap = new HashMap<>();

    // When expanded
    CommandDto resultRootWithNoExecuteSequenceActions = sut.expand(rootWithNoExecuteSequenceActions, commandMap);
    CommandDto resultRootWithExecuteSequenceActions = sut.expand(rootWithExecuteSequenceActions, commandMap);

    // Then the root is returned as-is
    assertThat(resultRootWithNoExecuteSequenceActions)
      .usingRecursiveComparison().isEqualTo(rootWithNoExecuteSequenceActions);
    assertThat(resultRootWithExecuteSequenceActions)
      .usingRecursiveComparison().isEqualTo(rootWithExecuteSequenceActions);

  }

}
