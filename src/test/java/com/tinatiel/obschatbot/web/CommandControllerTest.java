package com.tinatiel.obschatbot.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.data.command.CommandEntityService;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import com.tinatiel.obschatbot.data.command.model.action.ObsSourceVisibilityActionDto;
import com.tinatiel.obschatbot.data.command.model.action.SendMessageActionDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.InOrderSequencerDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.SequencerDto;
import com.tinatiel.obschatbot.security.owner.OwnerDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import com.tinatiel.obschatbot.web.command.CommandController;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@ContextConfiguration(classes = CommandController.class)
@WebMvcTest(CommandController.class)
public class CommandControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  CommandEntityService commandEntityService;

  @MockBean
  OwnerService ownerService;

  OwnerDto owner = OwnerDto.builder().id(UUID.randomUUID()).name("owner").build();

  @BeforeEach
  void setUp() {
    when(ownerService.getOwner()).thenReturn(owner);
  }

  @Test
  void getCommand() throws Exception {

    // Given a command exists
    CommandDto command = CommandDto.builder()
      .owner(owner.getId())
      .id(UUID.randomUUID())
      .name("foo")
      .sequencer(InOrderSequencerDto.builder().build())
      .disabled(true)
      .actions(Arrays.asList(
        SendMessageActionDto.builder().id(UUID.randomUUID()).position(1).message("howdy").build(),
        ObsSourceVisibilityActionDto.builder().id(UUID.randomUUID()).position(2).sceneName("foo").sourceName("bar").build(),
        SendMessageActionDto.builder().id(UUID.randomUUID()).position(3).message("there").build()
        ))
      .build();
    when(commandEntityService.findById(command.getId())).thenReturn(Optional.of(command));

    // Then it can be retrieved
    mockMvc.perform(
      get(WebConfig.BASE_PATH + "/command/{id}", command.getId())
      .accept(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(command.getId().toString()));

  }

  @Test
  void getNotFound() throws Exception {

    // Given a command doesn't exist
    when(commandEntityService.findById(any())).thenReturn(Optional.empty());

    // Then it isn't retrieved
    mockMvc.perform(
      get(WebConfig.BASE_PATH + "/command/{id}", UUID.randomUUID())
        .accept(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isNotFound());

  }

    @Test
  void listCommands() throws Exception {

    // Given commands are found
    CommandDto command1 = CommandDto.builder()
      .owner(owner.getId())
      .id(UUID.randomUUID())
      .build();
    CommandDto command2 = CommandDto.builder()
      .owner(owner.getId())
      .id(UUID.randomUUID())
      .build();
    CommandDto command3 = CommandDto.builder()
      .owner(owner.getId())
      .id(UUID.randomUUID())
      .build();
    when(commandEntityService.findByOwner(owner.getId())).thenReturn(Arrays.asList(command1, command2, command3));

    // When queried, they are returned
    mockMvc.perform(get(WebConfig.BASE_PATH + "/command")
    .accept(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(command1.getId().toString()))
      .andExpect(jsonPath("$[1].id").value(command2.getId().toString()))
      .andExpect(jsonPath("$[2].id").value(command3.getId().toString()));

  }
}
