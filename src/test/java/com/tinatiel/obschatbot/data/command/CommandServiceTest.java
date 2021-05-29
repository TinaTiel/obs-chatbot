package com.tinatiel.obschatbot.data.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.error.CyclicalActionsException;
import com.tinatiel.obschatbot.data.command.entity.CommandEntity;
import com.tinatiel.obschatbot.data.command.mapper.executable.ExecutableCommandMapper;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommandServiceTest {

  CommandEntityService commandEntityService;
  ExecutableCommandMapper mapper;

  CommandService commandService;

  @BeforeEach
  void setUp() {
    commandEntityService = mock(CommandEntityService.class);
    mapper = mock(ExecutableCommandMapper.class);
    commandService = new CommandServiceImpl(commandEntityService, mapper);
  }

  @Test
  void throwIfMapperCycles() {

    // Given a command is found
    when(commandEntityService.findByName(any())).thenReturn(Optional.of(mock(CommandDto.class)));

    // But given a cycle is encountered while mapping
    when(mapper.map(any())).thenThrow(new CyclicalActionsException("foo", null));

    assertThatThrownBy(() -> {
      commandService.findByName("doesntmatter");
    }).isInstanceOf(CyclicalActionsException.class);
  }

  @Test
  void noCommandFound() {

    // Given a command NOT found
    when(commandEntityService.findByName("doesntmatter")).thenReturn(Optional.empty());

    // When searched
    Optional<Command> result = commandService.findByName("doesntmatter");

    // Then nothing is returned
    assertThat(result).isEmpty();

  }
}
