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

import com.tinatiel.obschatbot.data.localuser.LocalUserAssignmentService;
import com.tinatiel.obschatbot.data.localuser.LocalUserAssignmentServiceImpl;
import com.tinatiel.obschatbot.data.localuser.model.LocalGroupDto;
import com.tinatiel.obschatbot.data.localuser.model.LocalUserGroupAssignmentDto;
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

@ContextConfiguration(classes = {UserAssignmentController.class, WebSecurityConfig.class})
@WebMvcTest(UserAssignmentController.class)
public class UserAssignmentControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  LocalUserAssignmentService localUserAssignmentService;

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
  void saveAssignment() throws Exception {

    // Given an assignment
    LocalUserGroupAssignmentDto assignment = LocalUserGroupAssignmentDto.builder()
      .groupId(UUID.randomUUID())
      .userId(UUID.randomUUID())
      .build();

    // Then it can be saved
    mockMvc.perform(
      put(WebConfig.BASE_PATH + "/user-assignment")
        .content("{\n"
          + "\t\"userId\": \"" + assignment.getUserId().toString() + "\",\n"
          + "\t\"groupId\": \"" + assignment.getGroupId().toString() + "\"\n"
          + "}")
      .contentType(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isOk());

    // And was called with the expected args
    ArgumentCaptor<LocalUserGroupAssignmentDto> argumentCaptor = ArgumentCaptor.forClass(LocalUserGroupAssignmentDto.class);
    verify(localUserAssignmentService).addAssignment(argumentCaptor.capture());
    assertThat(argumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(assignment);

  }

  @Test
  void removeAssignment() throws Exception {

    // Given an assignment
    LocalUserGroupAssignmentDto assignment = LocalUserGroupAssignmentDto.builder()
      .groupId(UUID.randomUUID())
      .userId(UUID.randomUUID())
      .build();

    // Then it can be saved
    mockMvc.perform(
      delete(WebConfig.BASE_PATH + "/user-assignment")
        .content("{\n"
          + "\t\"userId\": \"" + assignment.getUserId().toString() + "\",\n"
          + "\t\"groupId\": \"" + assignment.getGroupId().toString() + "\"\n"
          + "}")
        .contentType(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isOk());

    // And was called with the expected args
    ArgumentCaptor<LocalUserGroupAssignmentDto> argumentCaptor = ArgumentCaptor.forClass(LocalUserGroupAssignmentDto.class);
    verify(localUserAssignmentService).removeAssignment(argumentCaptor.capture());
    assertThat(argumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(assignment);

  }

}
