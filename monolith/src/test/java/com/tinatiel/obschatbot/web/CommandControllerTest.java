package com.tinatiel.obschatbot.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.data.command.CommandEntityService;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import com.tinatiel.obschatbot.data.command.model.action.ObsSourceVisibilityActionDto;
import com.tinatiel.obschatbot.data.command.model.action.SendMessageActionDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.InOrderSequencerDto;
import com.tinatiel.obschatbot.security.WebSecurityConfig;
import com.tinatiel.obschatbot.security.owner.OwnerDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {CommandController.class, WebSecurityConfig.class})
@WebMvcTest(CommandController.class)
public class CommandControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  CommandEntityService commandEntityService;

  @MockBean
  OwnerService ownerService;

  @MockBean
  ClientRegistrationRepository ignoreme;

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

  @Test
  void createCommand() throws Exception {

    // Given a new command is saved
    CommandDto command = CommandDto.builder()
      .owner(owner.getId())
      .id(UUID.randomUUID())
      .build();
    when(commandEntityService.save(any(CommandDto.class))).thenReturn(command);

    // When put without an id
    // Then it is created
    mockMvc.perform(put(WebConfig.BASE_PATH + "/command/{id}", command.getId())
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\n"
        + "  \"id\": \"" + command.getId() + "\",\n"
        + "  \"name\": \"somecommand\",\n"
        + "  \"sequencer\": {\n"
        + "    \"@type\": \"ordered\",\n"
        + "    \"id\": \"a10ca5a8-e6df-11eb-ba80-0242ac130004\",\n"
        + "    \"reversed\": false\n"
        + "  }\n"
        + "}"))
      .andDo(print())
      .andExpect(status().isOk());

    // And was called with the expected args
    ArgumentCaptor<CommandDto> argumentCaptor = ArgumentCaptor.forClass(CommandDto.class);
    verify(commandEntityService).save(argumentCaptor.capture());
    CommandDto actual = argumentCaptor.getValue();
    assertThat(actual.getId()).isEqualTo(command.getId());
    assertThat(actual.getOwner()).isEqualTo(owner.getId());

  }

  @Test
  void updateCommand() throws Exception {

    // Given a command is saved
    CommandDto command = CommandDto.builder()
      .owner(owner.getId())
      .id(UUID.randomUUID())
      .build();
    when(commandEntityService.save(any(CommandDto.class))).thenReturn(command);

    // When put
    // Then it is created
    mockMvc.perform(put(WebConfig.BASE_PATH + "/command/{id}", command.getId())
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\n"
        + "  \"id\": \"" + command.getId() + "\",\n"
        + "  \"name\": \"somecommand\",\n"
        + "  \"sequencer\": {\n"
        + "    \"@type\": \"ordered\",\n"
        + "    \"id\": \"a10ca5a8-e6df-11eb-ba80-0242ac130004\",\n"
        + "    \"reversed\": false\n"
        + "  }\n"
        + "}"))
      .andDo(print())
      .andExpect(status().isOk());

    // And was called with the expected args
    ArgumentCaptor<CommandDto> argumentCaptor = ArgumentCaptor.forClass(CommandDto.class);
    verify(commandEntityService).save(argumentCaptor.capture());
    CommandDto actual = argumentCaptor.getValue();
    assertThat(actual.getId()).isEqualTo(command.getId());
    assertThat(actual.getOwner()).isEqualTo(command.getOwner());
  }

  @Test
  void deleteCommand() throws Exception {
    UUID id = UUID.randomUUID();
    mockMvc.perform(delete(WebConfig.BASE_PATH + "/command/{id}", id))
      .andDo(print())
      .andExpect(status().isOk());

    // And was called with the expected args
    ArgumentCaptor<UUID> argumentCaptor = ArgumentCaptor.forClass(UUID.class);
    verify(commandEntityService).delete(argumentCaptor.capture());
    assertThat(argumentCaptor.getValue()).isEqualTo(id);
  }

}
