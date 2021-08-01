package com.tinatiel.obschatbot.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinatiel.obschatbot.data.client.twitch.auth.TwitchClientAuthDataService;
import com.tinatiel.obschatbot.data.client.twitch.auth.model.TwitchClientAuthDataDto;
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

@ContextConfiguration(classes = {TwitchAuthController.class, WebSecurityConfig.class})
@WebMvcTest(TwitchAuthController.class)
public class TwitchAuthClientControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  TwitchClientAuthDataService twitchClientAuthDataService;

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
  void getTwitchAuthSettings() throws Exception {

    // Given settings exist
    TwitchClientAuthDataDto settings = TwitchClientAuthDataDto.builder()
      .owner(owner.getId())
      .clientId("clientid")
      .clientSecret("clientsecret")
      .build();
    when(twitchClientAuthDataService.findByOwner(owner.getId())).thenReturn(Optional.of(settings));

    // They can be retrieved
    mockMvc.perform(get(WebConfig.BASE_PATH + "/twitch/auth/settings")
        .accept(MediaType.APPLICATION_JSON)
      ).andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.owner").value(owner.getId().toString()))
      .andExpect(jsonPath("$.clientId").value("clientid"))
      .andExpect(jsonPath("$.clientSecret").value("clientsecret"));

  }

  @Test
  void getTwitchAuthSettingsNotFound() throws Exception {

    // Given settings don't exist
    when(twitchClientAuthDataService.findByOwner(owner.getId())).thenReturn(Optional.empty());

    // when called
    mockMvc.perform(get(WebConfig.BASE_PATH + "/twitch/auth/settings")
        .accept(MediaType.APPLICATION_JSON)
      ).andDo(print())
      .andExpect(status().isNotFound());

    // And the service was called
    verify(twitchClientAuthDataService).findByOwner(owner.getId());

  }

  @Test
  void saveTwitchAuthSettings() throws Exception {

    // Given settings
    TwitchClientAuthDataDto settings = TwitchClientAuthDataDto.builder()
      .owner(owner.getId())
      .clientId("clientid")
      .clientSecret("newclientsecret")
      .build();

    // When saved it is ok
    mockMvc.perform(put(WebConfig.BASE_PATH + "/twitch/auth/settings")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(settings))
      ).andDo(print())
      .andExpect(status().isOk());

    // And the service was called with the expected settings
    ArgumentCaptor<TwitchClientAuthDataDto> captor = ArgumentCaptor.forClass(TwitchClientAuthDataDto.class);
    verify(twitchClientAuthDataService).save(captor.capture());
    TwitchClientAuthDataDto actual = captor.getValue();
    assertThat(actual).usingRecursiveComparison().ignoringFields("owner").isEqualTo(settings);
    assertThat(actual.getOwner()).isEqualTo(owner.getId());

  }

}
