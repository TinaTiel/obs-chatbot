package com.tinatiel.obschatbot.commandservice.expand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.commandservice.command.CommandDto;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class CommandExpandServiceTest {

  @Mock
  CommandExtractor commandExtractor;

  @Mock
  CommandExpander commandExpander;

  CommandExpandService sut;

  @BeforeEach
  void setUp() {
    sut = new CommandExpandServiceImpl(commandExtractor, commandExpander);
  }

  @Test
  void shouldThrowIfFunctionThrows() {

    // Given the function returns an error
    Exception error = new RuntimeException("some exception");
    when(commandExtractor.extract(any())).thenReturn(Mono.error(error));

    // When expanded
    // Then an exception is thrown
    assertThatThrownBy(() -> {
      sut.expand(CommandDto.builder().build()).block();
    }).isEqualTo(error);

  }

  @Test
  void shouldThrowIfExpanderThrows() {

    // Given the function returns normally
    when(commandExtractor.extract(any())).thenReturn(Mono.just(new HashMap<>()));

    // But given the expander throws
    Exception error = new RuntimeException("some exception");
    when(commandExpander.expand(any(), any())).thenThrow(error);

    // When expanded
    // Then an exception is thrown
    assertThatThrownBy(() -> {
      sut.expand(CommandDto.builder().build()).block();
    }).isEqualTo(error);

  }

  @Test
  void shouldReturnCommandWithNothingToExpand() {

    // Given the function returns no commands to expand with
    // (e.g. a Command that has no ExecuteSequence actions on it)
    when(commandExtractor.extract(any())).thenReturn(Mono.just(new HashMap<>()));

    // And given the expander does something with an empty map anyway
    CommandDto expected = CommandDto.builder().id(UUID.randomUUID()).build();
    when(commandExpander.expand(any(), any())).thenReturn(expected);

    // When expanded
    CommandDto result = sut.expand(CommandDto.builder().build()).block();

    // Then it matches as expected
    assertThat(result).usingRecursiveComparison().isEqualTo(expected);

  }

  @Test
  void shouldExpandAsExpectedWhenAllWorksAsExpected() {

    // Given the function returns some commands to expand with
    Map<UUID, CommandDto> commandMap = Map.of(UUID.randomUUID(), CommandDto.builder().id(UUID.randomUUID()).build());
    when(commandExtractor.extract(any())).thenReturn(Mono.just(commandMap));

    // And given the expander does something with that information
    CommandDto expected = CommandDto.builder().id(UUID.randomUUID()).build();
    when(commandExpander.expand(any(), eq(commandMap))).thenReturn(expected);

    // When expanded
    CommandDto result = sut.expand(CommandDto.builder().build()).block();

    // Then it matches as expected
    assertThat(result).usingRecursiveComparison().isEqualTo(expected);

  }

}
