package com.tinatiel.obschatbot.core.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.data.command.CommandEntityService;
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
  void foundWhenExistsAndMappable() {
    CommandDto dto = mock(CommandDto.class);
    Command command = mock(Command.class);
    when(commandEntityService.findByName("foo")).thenReturn(Optional.of(dto));
    when(mapper.map(dto)).thenReturn(command);

    // When searched
    Optional<Command> result = commandService.findByName("foo");

    // Then it is found
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(command);

  }

}
