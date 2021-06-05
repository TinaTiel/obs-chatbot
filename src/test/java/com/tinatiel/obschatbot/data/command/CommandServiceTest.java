package com.tinatiel.obschatbot.data.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.error.CyclicalActionsException;
import com.tinatiel.obschatbot.data.command.mapper.executable.ExecutableCommandMapper;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import com.tinatiel.obschatbot.security.owner.OwnerDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommandServiceTest {

  CommandEntityService commandEntityService;
  ExecutableCommandMapper mapper;
  OwnerService ownerService;

  CommandService commandService;

  @BeforeEach
  void setUp() {
    commandEntityService = mock(CommandEntityService.class);
    mapper = mock(ExecutableCommandMapper.class);
    ownerService = mock(OwnerService.class);
    commandService = new CommandServiceImpl(ownerService, commandEntityService, mapper);

    // ownerService returns an owner
    OwnerDto ownerDto = OwnerDto.builder().name("some owner").id(UUID.randomUUID()).build();
    when(ownerService.getOwner()).thenReturn(ownerDto);
  }

  @Test
  void throwIfMapperCycles() {

    // Given a command is found
    when(commandEntityService.findByNameAndOwner(any(), any())).thenReturn(Optional.of(mock(CommandDto.class)));

    // But given a cycle is encountered while mapping
    when(mapper.map(any())).thenThrow(new CyclicalActionsException("foo", null));

    assertThatThrownBy(() -> {
      commandService.findByName("doesntmatter");
    }).isInstanceOf(CyclicalActionsException.class);
  }

  @Test
  void noCommandFound() {

    // Given a command NOT found
    when(commandEntityService.findByNameAndOwner("doesntmatter", UUID.randomUUID())).thenReturn(Optional.empty());

    // When searched
    Optional<Command> result = commandService.findByName("doesntmatter");

    // Then nothing is returned
    assertThat(result).isEmpty();

  }
}
