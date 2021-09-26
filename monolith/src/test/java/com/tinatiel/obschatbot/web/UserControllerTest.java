package com.tinatiel.obschatbot.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import com.tinatiel.obschatbot.data.command.model.action.ObsSourceVisibilityActionDto;
import com.tinatiel.obschatbot.data.command.model.action.SendMessageActionDto;
import com.tinatiel.obschatbot.data.command.model.sequencer.InOrderSequencerDto;
import com.tinatiel.obschatbot.data.localuser.LocalUserService;
import com.tinatiel.obschatbot.data.localuser.model.LocalGroupDto;
import com.tinatiel.obschatbot.data.localuser.model.LocalUserDto;
import com.tinatiel.obschatbot.security.WebSecurityConfig;
import com.tinatiel.obschatbot.security.owner.OwnerDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import com.tinatiel.obschatbot.web.error.GlobalControllerErrorAdvice;
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

@ContextConfiguration(classes = {UserController.class, WebSecurityConfig.class})
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  LocalUserService localUserService;

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
  void getUser() throws Exception {

    // Given an user exists
    LocalUserDto user = LocalUserDto.builder()
      .owner(owner.getId())
      .id(UUID.randomUUID())
      .broadcaster(true)
      .username("foo")
      .groups(Arrays.asList(
        LocalGroupDto.builder().id(UUID.randomUUID()).name("bar").build()
      ))
      .build();
    when(localUserService.findById(user.getId())).thenReturn(Optional.of(user));

    // Then it can be retrieved
    mockMvc.perform(
      get(WebConfig.BASE_PATH + "/user/{id}", user.getId())
      .accept(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(user.getId().toString()))
      .andExpect(jsonPath("$.groups[0].id").value(user.getGroups().get(0).getId().toString()));

  }

  @Test
  void getNotFound() throws Exception {

    // Given a command doesn't exist
    when(localUserService.findById(any())).thenReturn(Optional.empty());

    // Then it isn't retrieved
    mockMvc.perform(
      get(WebConfig.BASE_PATH + "/user/{id}", UUID.randomUUID())
        .accept(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isNotFound());

  }

  @Test
  void list() throws Exception {

    // Given users are found
    LocalUserDto user1 = LocalUserDto.builder().id(UUID.randomUUID()).build();
    LocalUserDto user2 = LocalUserDto.builder().id(UUID.randomUUID()).build();
    LocalUserDto user3 = LocalUserDto.builder().id(UUID.randomUUID()).build();
    when(localUserService.findByOwner(owner.getId())).thenReturn(Arrays.asList(user1, user2, user3));

    // When queried, they are returned
    mockMvc.perform(get(WebConfig.BASE_PATH + "/user")
    .accept(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(user1.getId().toString()))
      .andExpect(jsonPath("$[1].id").value(user2.getId().toString()))
      .andExpect(jsonPath("$[2].id").value(user3.getId().toString()));

  }

  @Test
  void create() throws Exception {

    // Given a new User is saved
    LocalUserDto user = LocalUserDto.builder()
      .owner(owner.getId())
      .id(UUID.randomUUID())
      .broadcaster(true)
      .platform(Platform.TWITCH)
      .username("foo")
      .build();
    when(localUserService.save(any(LocalUserDto.class))).thenReturn(user);

    // When put
    // Then it is created
    mockMvc.perform(put(WebConfig.BASE_PATH + "/user/{id}", user.getId())
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\n"
        + "  \"id\": \"" + UUID.randomUUID().toString() + "\",\n"
        + "  \"platform\": \"" + "TWITCH" + "\",\n"
        + "  \"username\": \"foo\"\n"
        + "}"))
      .andDo(print())
      .andExpect(status().isOk());

    // And was called with the expected args
    ArgumentCaptor<LocalUserDto> argumentCaptor = ArgumentCaptor.forClass(LocalUserDto.class);
    verify(localUserService).save(argumentCaptor.capture());
    LocalUserDto actual = argumentCaptor.getValue();
    assertThat(actual.getId()).isEqualTo(user.getId());
    assertThat(actual.getOwner()).isEqualTo(owner.getId());

  }

  @Test
  void update() throws Exception {

    // Given a User is saved
    LocalUserDto user = LocalUserDto.builder()
      .owner(owner.getId())
      .id(UUID.randomUUID())
      .broadcaster(true)
      .platform(Platform.TWITCH)
      .username("foo")
      .build();
    when(localUserService.save(any(LocalUserDto.class))).thenReturn(user);

    // When put
    // Then it is updated
    mockMvc.perform(put(WebConfig.BASE_PATH + "/user/{id}", user.getId())
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\n"
        + "  \"id\": \"" + user.getId().toString() + "\",\n"
        + "  \"platform\": \"" + user.getPlatform() + "\",\n"
        + "  \"username\": \"foo\"\n"
        + "}"))
      .andDo(print())
      .andExpect(status().isOk());

    // And was called with the expected args
    ArgumentCaptor<LocalUserDto> argumentCaptor = ArgumentCaptor.forClass(LocalUserDto.class);
    verify(localUserService).save(argumentCaptor.capture());
    LocalUserDto actual = argumentCaptor.getValue();
    assertThat(actual.getId()).isEqualTo(user.getId());
    assertThat(actual.getOwner()).isEqualTo(owner.getId());
  }

  @Test
  void deleteUser() throws Exception {
    UUID id = UUID.randomUUID();
    mockMvc.perform(delete(WebConfig.BASE_PATH + "/user/{id}", id))
      .andDo(print())
      .andExpect(status().isOk());

    // And was called with the expected args
    ArgumentCaptor<UUID> argumentCaptor = ArgumentCaptor.forClass(UUID.class);
    verify(localUserService).delete(argumentCaptor.capture());
    assertThat(argumentCaptor.getValue()).isEqualTo(id);
  }

}
