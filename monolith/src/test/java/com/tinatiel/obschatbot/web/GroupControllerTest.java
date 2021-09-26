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

import com.tinatiel.obschatbot.data.localuser.LocalGroupService;
import com.tinatiel.obschatbot.data.localuser.model.LocalGroupDto;
import com.tinatiel.obschatbot.data.localuser.model.LocalUserDto;
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

@ContextConfiguration(classes = {GroupController.class, WebSecurityConfig.class})
@WebMvcTest(GroupController.class)
public class GroupControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  LocalGroupService localGroupService;

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

    // Given a group exists
    LocalGroupDto group = LocalGroupDto.builder()
      .owner(owner.getId())
      .id(UUID.randomUUID())
      .name("some group")
      .build();
    when(localGroupService.findById(group.getId())).thenReturn(Optional.of(group));

    // Then it can be retrieved
    mockMvc.perform(
      get(WebConfig.BASE_PATH + "/group/{id}", group.getId())
      .accept(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(group.getId().toString()));

  }

  @Test
  void getNotFound() throws Exception {

    // Given a command doesn't exist
    when(localGroupService.findById(any())).thenReturn(Optional.empty());

    // Then it isn't retrieved
    mockMvc.perform(
      get(WebConfig.BASE_PATH + "/group/{id}", UUID.randomUUID())
        .accept(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isNotFound());

  }

  @Test
  void list() throws Exception {

    // Given users are found
    LocalGroupDto group1 = LocalGroupDto.builder().id(UUID.randomUUID()).build();
    LocalGroupDto group2 = LocalGroupDto.builder().id(UUID.randomUUID()).build();
    LocalGroupDto group3 = LocalGroupDto.builder().id(UUID.randomUUID()).build();
    when(localGroupService.findByOwner(owner.getId())).thenReturn(Arrays.asList(group1, group2, group3));

    // When queried, they are returned
    mockMvc.perform(get(WebConfig.BASE_PATH + "/group")
    .accept(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(group1.getId().toString()))
      .andExpect(jsonPath("$[1].id").value(group2.getId().toString()))
      .andExpect(jsonPath("$[2].id").value(group3.getId().toString()));

  }

  @Test
  void create() throws Exception {

    // Given a new User is saved
    LocalGroupDto group = LocalGroupDto.builder()
      .owner(owner.getId())
      .id(UUID.randomUUID())
      .name("some group")
      .build();
    when(localGroupService.save(any(LocalGroupDto.class))).thenReturn(group);

    // When put
    // Then it is created
    mockMvc.perform(put(WebConfig.BASE_PATH + "/group/{id}", group.getId())
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\n"
        + "  \"id\": \"" + UUID.randomUUID().toString() + "\",\n"
        + "  \"name\": \"foo\"\n"
        + "}"))
      .andDo(print())
      .andExpect(status().isOk());

    // And was called with the expected args
    ArgumentCaptor<LocalGroupDto> argumentCaptor = ArgumentCaptor.forClass(LocalGroupDto.class);
    verify(localGroupService).save(argumentCaptor.capture());
    LocalGroupDto actual = argumentCaptor.getValue();
    assertThat(actual.getId()).isEqualTo(group.getId());
    assertThat(actual.getOwner()).isEqualTo(owner.getId());

  }

  @Test
  void update() throws Exception {

    // Given a User is saved
    LocalGroupDto user = LocalGroupDto.builder()
      .owner(owner.getId())
      .id(UUID.randomUUID())
      .name("some group")
      .build();
    when(localGroupService.save(any(LocalGroupDto.class))).thenReturn(user);

    // When put
    // Then it is updated
    mockMvc.perform(put(WebConfig.BASE_PATH + "/group/{id}", user.getId())
      .contentType(MediaType.APPLICATION_JSON)
      .content("{\n"
        + "  \"id\": \"" + user.getId().toString() + "\",\n"
        + "  \"name\": \"foo\"\n"
        + "}"))
      .andDo(print())
      .andExpect(status().isOk());

    // And was called with the expected args
    ArgumentCaptor<LocalGroupDto> argumentCaptor = ArgumentCaptor.forClass(LocalGroupDto.class);
    verify(localGroupService).save(argumentCaptor.capture());
    LocalGroupDto actual = argumentCaptor.getValue();
    assertThat(actual.getId()).isEqualTo(user.getId());
    assertThat(actual.getOwner()).isEqualTo(owner.getId());
  }

  @Test
  void deleteUser() throws Exception {
    UUID id = UUID.randomUUID();
    mockMvc.perform(delete(WebConfig.BASE_PATH + "/group/{id}", id))
      .andDo(print())
      .andExpect(status().isOk());

    // And was called with the expected args
    ArgumentCaptor<UUID> argumentCaptor = ArgumentCaptor.forClass(UUID.class);
    verify(localGroupService).delete(argumentCaptor.capture());
    assertThat(argumentCaptor.getValue()).isEqualTo(id);
  }

}
