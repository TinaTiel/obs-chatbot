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
import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.data.client.obs.ObsClientDataService;
import com.tinatiel.obschatbot.data.client.obs.model.ObsClientSettingsDto;
import com.tinatiel.obschatbot.security.WebSecurityConfig;
import com.tinatiel.obschatbot.security.owner.OwnerDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import com.tinatiel.obschatbot.web.model.ClientStatusRequestDto;
import com.tinatiel.obschatbot.web.model.ClientStatusRequestDto.State;
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

@ContextConfiguration(classes = {ObsController.class, WebSecurityConfig.class})
@WebMvcTest(ObsController.class)
public class ObsClientControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  ObsClientDataService obsClientDataService;
  @MockBean
  ClientManager obsClientManager;

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
  void getObsSettings() throws Exception {

    // Given settings exist
    ObsClientSettingsDto settings = ObsClientSettingsDto.builder()
      .owner(owner.getId())
      .host("localhost")
      .password("password")
      .port(4444)
      .connectionTimeoutMs(1234)
      .build();
    when(obsClientDataService.findByOwner(owner.getId())).thenReturn(Optional.of(settings));

    // They can be retrieved
    mockMvc.perform(get(WebConfig.BASE_PATH + "/obs/settings")
        .accept(MediaType.APPLICATION_JSON)
      ).andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.owner").value(owner.getId().toString()))
      .andExpect(jsonPath("$.host").value("localhost"))
      .andExpect(jsonPath("$.password").value("password"))
      .andExpect(jsonPath("$.port").value(4444))
      .andExpect(jsonPath("$.connectionTimeoutMs").value(1234));

  }

  @Test
  void getObsSettingsNotFound() throws Exception {

    // Given settings don't exist
    when(obsClientDataService.findByOwner(owner.getId())).thenReturn(Optional.empty());

    // when called
    mockMvc.perform(get(WebConfig.BASE_PATH + "/obs/settings")
        .accept(MediaType.APPLICATION_JSON)
      ).andDo(print())
      .andExpect(status().isNotFound());

    // And the service was called
    verify(obsClientDataService).findByOwner(owner.getId());

  }

  @Test
  void saveObsSettings() throws Exception {

    // Given settings
    ObsClientSettingsDto settings = ObsClientSettingsDto.builder()
      .owner(owner.getId())
      .host("localhost")
      .password("password")
      .port(4444)
      .connectionTimeoutMs(1234)
      .build();

    // When saved it is ok
    mockMvc.perform(put(WebConfig.BASE_PATH + "/obs/settings")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(settings))
      ).andDo(print())
      .andExpect(status().isOk());

    // And the service was called with the expected settings
    ArgumentCaptor<ObsClientSettingsDto> captor = ArgumentCaptor.forClass(ObsClientSettingsDto.class);
    verify(obsClientDataService).save(captor.capture());
    ObsClientSettingsDto actual = captor.getValue();
    assertThat(actual).usingRecursiveComparison().ignoringFields("owner").isEqualTo(settings);
    assertThat(actual.getOwner()).isEqualTo(owner.getId());

  }

  @Test
  void startObs() throws Exception {

    // Given status request
    ClientStatusRequestDto settings = ClientStatusRequestDto.builder()
      .state(State.START)
      .build();

    // When saved it is ok
    mockMvc.perform(put(WebConfig.BASE_PATH + "/obs/status")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(settings))
      ).andDo(print())
      .andExpect(status().isOk());

    // And the service was started
    verify(obsClientManager).startClient();

  }

  @Test
  void stopObs() throws Exception {

    // Given status request
    ClientStatusRequestDto settings = ClientStatusRequestDto.builder()
      .state(State.STOP)
      .build();

    // When saved it is ok
    mockMvc.perform(put(WebConfig.BASE_PATH + "/obs/status")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(settings))
      ).andDo(print())
      .andExpect(status().isOk());

    // And the service was started
    verify(obsClientManager).stopClient();

  }

  @Test
  void restartObs() throws Exception {

    // Given status request
    ClientStatusRequestDto settings = ClientStatusRequestDto.builder()
      .state(State.RESTART)
      .build();

    // When saved it is ok
    mockMvc.perform(put(WebConfig.BASE_PATH + "/obs/status")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(settings))
      ).andDo(print())
      .andExpect(status().isOk());

    // And the service was started
    verify(obsClientManager).reloadClient();

  }

}
