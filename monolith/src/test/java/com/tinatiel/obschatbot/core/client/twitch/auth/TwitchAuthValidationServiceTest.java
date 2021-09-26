package com.tinatiel.obschatbot.core.client.twitch.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.core.client.twitch.auth.messaging.TwitchAuthClientMessagingGateway;
import com.tinatiel.obschatbot.core.client.twitch.auth.messaging.TwitchAuthValidationFailureEvent;
import com.tinatiel.obschatbot.core.client.twitch.auth.messaging.TwitchAuthValidationSuccessEvent;
import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import com.tinatiel.obschatbot.security.owner.OwnerDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

public class TwitchAuthValidationServiceTest {

  ArgumentCaptor<ObsChatbotEvent> argumentCaptor;

  OwnerService ownerService;
  OwnerDto owner;
  OAuth2AuthorizedClientService authorizedClientService;
  OAuth2AuthorizedClientManager authorizedClientManager;
  TwitchAuthClientMessagingGateway twitchAuthQueueClient;
  TwitchAuthClient twitchAuthClient;

  TwitchAuthValidationService twitchAuthValidationService;

  @BeforeEach
  void setUp() {
    ownerService = mock(OwnerService.class);
    owner = (OwnerDto.builder()
      .id(UUID.randomUUID())
      .name("foo")
      .build());
    when(ownerService.getOwner()).thenReturn(owner);
    authorizedClientService = mock(OAuth2AuthorizedClientService.class);
    authorizedClientManager = mock(OAuth2AuthorizedClientManager.class);
    twitchAuthQueueClient = mock(TwitchAuthClientMessagingGateway.class);
    twitchAuthClient = mock(TwitchAuthClient.class);

    argumentCaptor = ArgumentCaptor.forClass(ObsChatbotEvent.class);
    doNothing().when(twitchAuthQueueClient).submit(argumentCaptor.capture());

    twitchAuthValidationService = new TwitchAuthValidationService(
      ownerService, authorizedClientService,
      authorizedClientManager,
      twitchAuthQueueClient,
      twitchAuthClient
    );
  }

  @Test
  void whenTokenValidThenSendSuccessMessage() {

    // Given the TwitchApiClient finds token is valid
    when(twitchAuthClient.isCurrentAccessTokenValid()).thenReturn(true);

    // When called
    twitchAuthValidationService.validateToken();

    // Then a validation event will be submitted to the queue
    assertThat(argumentCaptor.getValue()).isInstanceOf(TwitchAuthValidationSuccessEvent.class);

  }

  @Test
  void whenTokenInValidThenSendFailMessage() {

    // Given the TwitchApiClient finds token is valid
    when(twitchAuthClient.isCurrentAccessTokenValid()).thenReturn(false);

    // When called
    twitchAuthValidationService.validateToken();

    // Then a validation event will be submitted to the queue
    assertThat(argumentCaptor.getValue()).isInstanceOf(TwitchAuthValidationFailureEvent.class);

  }

}
