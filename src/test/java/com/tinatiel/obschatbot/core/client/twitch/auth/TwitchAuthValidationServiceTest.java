package com.tinatiel.obschatbot.core.client.twitch.auth;

import static org.mockito.Mockito.mock;

import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import com.tinatiel.obschatbot.core.messaging.QueueClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.client.RestTemplate;

public class TwitchAuthValidationServiceTest {

  OAuth2AuthorizedClientService authorizedClientService;
  OAuth2AuthorizedClientManager authorizedClientManager;
  RestTemplate restTemplate;
  TwitchAuthConnectionSettingsFactory twitchAuthConnectionSettingsFactory;
  QueueClient<ObsChatbotEvent> twitchAuthQueueClient;

  TwitchAuthValidationService twitchAuthValidationService;

  @BeforeEach
  void setUp() {
    authorizedClientService = mock(OAuth2AuthorizedClientService.class);
    authorizedClientManager = mock(OAuth2AuthorizedClientManager.class);
    restTemplate = mock(RestTemplate.class);
    twitchAuthConnectionSettingsFactory = mock(TwitchAuthConnectionSettingsFactory.class);
    twitchAuthQueueClient = mock(QueueClient.class);

    twitchAuthValidationService = new TwitchAuthValidationService(
      authorizedClientService,
      authorizedClientManager,
      restTemplate,
      twitchAuthConnectionSettingsFactory,
      twitchAuthQueueClient
    );
  }

  @Test
  void tokenIsValid() {

  }
}
