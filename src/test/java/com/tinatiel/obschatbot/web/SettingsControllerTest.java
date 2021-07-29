package com.tinatiel.obschatbot.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinatiel.obschatbot.data.localuser.LocalUserService;
import com.tinatiel.obschatbot.data.system.SystemSettingsDataService;
import com.tinatiel.obschatbot.data.system.entity.SystemSettingsEntity;
import com.tinatiel.obschatbot.data.system.model.SystemSettingsDto;
import com.tinatiel.obschatbot.security.WebSecurityConfig;
import com.tinatiel.obschatbot.security.owner.OwnerDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
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

@ContextConfiguration(classes = {SettingsController.class, WebSecurityConfig.class})
@WebMvcTest(SettingsController.class)
public class SettingsControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  SystemSettingsDataService systemSettingsDataService;

  @MockBean
  OwnerService ownerService;

  @MockBean
  ClientRegistrationRepository ignoreme;

  ObjectMapper objectMapper = new ObjectMapper();

  OwnerDto owner = OwnerDto.builder().id(UUID.randomUUID()).name("owner").build();

  @BeforeEach
  void setUp() {
    when(ownerService.getOwner()).thenReturn(owner);
  }

  @Test
  void getSystemSettings() throws Exception {

    // Given settings exist
    SystemSettingsDto settings = SystemSettingsDto.builder()
      .owner(owner.getId())
      .maxActionBatchSize(123)
      .recursionTimeoutMillis(456)
      .build();
    when(systemSettingsDataService.findByOwner(owner.getId())).thenReturn(Optional.of(settings));

    // They can be retrieved
    mockMvc.perform(get(WebConfig.BASE_PATH + "/settings/system")
      .accept(MediaType.APPLICATION_JSON)
    ).andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.owner").value(owner.getId().toString()))
      .andExpect(jsonPath("$.maxActionBatchSize").value(123))
      .andExpect(jsonPath("$.recursionTimeoutMillis").value(456));

  }

  @Test
  void getSystemSettingsNotFound() throws Exception {

    // Given settings don't exist
    when(systemSettingsDataService.findByOwner(owner.getId())).thenReturn(Optional.empty());

    // when called
    mockMvc.perform(get(WebConfig.BASE_PATH + "/settings/system")
        .accept(MediaType.APPLICATION_JSON)
      ).andDo(print())
      .andExpect(status().isNotFound());

    // And the service was called
    verify(systemSettingsDataService).findByOwner(owner.getId());

  }

  @Test
  void saveSystemSettings() throws Exception {

    // Given settings
    SystemSettingsDto settings = SystemSettingsDto.builder()
      .maxActionBatchSize(123)
      .recursionTimeoutMillis(456)
      .build();

    // When saved it is ok
    mockMvc.perform(put(WebConfig.BASE_PATH + "/settings/system")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(settings))
    ).andDo(print())
      .andExpect(status().isOk());

    // And the service was called with the expected settings
    ArgumentCaptor<SystemSettingsDto> captor = ArgumentCaptor.forClass(SystemSettingsDto.class);
    verify(systemSettingsDataService).save(captor.capture());
    SystemSettingsDto actual = captor.getValue();
    assertThat(actual).usingRecursiveComparison().ignoringFields("owner").isEqualTo(settings);
    assertThat(actual.getOwner()).isEqualTo(owner.getId());

  }

}
