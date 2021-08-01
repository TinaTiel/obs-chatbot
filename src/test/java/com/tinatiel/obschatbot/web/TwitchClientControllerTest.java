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
import com.tinatiel.obschatbot.data.client.twitch.chat.TwitchClientChatDataService;
import com.tinatiel.obschatbot.data.client.twitch.chat.model.TwitchClientChatDataDto;
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

@ContextConfiguration(classes = {TwitchController.class, WebSecurityConfig.class})
@WebMvcTest(TwitchController.class)
public class TwitchClientControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  TwitchClientChatDataService twitchClientChatDataService;

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
  void getTwitchSettings() throws Exception {

    // Given settings exist
    TwitchClientChatDataDto settings = TwitchClientChatDataDto.builder()
      .owner(owner.getId())
      .broadcasterChannelUsername("broadcaster")
      .trigger("!")
      .parseEntireMessage(false)
      .connectionAttempts(3)
      .connectionTimeoutMs(1234)
      .joinMessage("...has joined")
      .leaveMessage("...has left")
      .build();
    when(twitchClientChatDataService.findByOwner(owner.getId())).thenReturn(Optional.of(settings));

    // They can be retrieved
    mockMvc.perform(get(WebConfig.BASE_PATH + "/settings/twitch")
        .accept(MediaType.APPLICATION_JSON)
      ).andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.owner").value(owner.getId().toString()))
      .andExpect(jsonPath("$.broadcasterChannelUsername").value("broadcaster"))
      .andExpect(jsonPath("$.trigger").value("!"))
      .andExpect(jsonPath("$.parseEntireMessage").value(false))
      .andExpect(jsonPath("$.connectionAttempts").value(3))
      .andExpect(jsonPath("$.connectionTimeoutMs").value(1234))
      .andExpect(jsonPath("$.joinMessage").value("...has joined"))
      .andExpect(jsonPath("$.leaveMessage").value("...has left"));

  }

  @Test
  void getTwitchSettingsNotFound() throws Exception {

    // Given settings don't exist
    when(twitchClientChatDataService.findByOwner(owner.getId())).thenReturn(Optional.empty());

    // when called
    mockMvc.perform(get(WebConfig.BASE_PATH + "/settings/twitch")
        .accept(MediaType.APPLICATION_JSON)
      ).andDo(print())
      .andExpect(status().isNotFound());

    // And the service was called
    verify(twitchClientChatDataService).findByOwner(owner.getId());

  }

  @Test
  void saveTwitchSettings() throws Exception {

    // Given settings
    TwitchClientChatDataDto settings = TwitchClientChatDataDto.builder()
      .owner(owner.getId())
      .broadcasterChannelUsername("newbroadcaster")
      .trigger("#")
      .parseEntireMessage(true)
      .connectionAttempts(30)
      .connectionTimeoutMs(12340)
      .joinMessage("...joined")
      .leaveMessage("...left")
      .build();

    // When saved it is ok
    mockMvc.perform(put(WebConfig.BASE_PATH + "/settings/twitch")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(settings))
      ).andDo(print())
      .andExpect(status().isOk());

    // And the service was called with the expected settings
    ArgumentCaptor<TwitchClientChatDataDto> captor = ArgumentCaptor.forClass(TwitchClientChatDataDto.class);
    verify(twitchClientChatDataService).save(captor.capture());
    TwitchClientChatDataDto actual = captor.getValue();
    assertThat(actual).usingRecursiveComparison().ignoringFields("owner").isEqualTo(settings);
    assertThat(actual.getOwner()).isEqualTo(owner.getId());

  }

}
