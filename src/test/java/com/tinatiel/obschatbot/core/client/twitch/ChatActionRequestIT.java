package com.tinatiel.obschatbot.core.client.twitch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.core.SpringIntegrationTestConfig;
import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.client.ActionCommandConsumer;
import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;
import com.tinatiel.obschatbot.core.client.event.ClientReadyEvent;
import com.tinatiel.obschatbot.core.client.twitch.chat.TwitchChatClientConfig;
import com.tinatiel.obschatbot.core.client.twitch.chat.TwitchChatClientDelegate;
import com.tinatiel.obschatbot.core.client.twitch.chat.TwitchChatClientFactory;
import com.tinatiel.obschatbot.core.client.twitch.chat.TwitchChatClientSettings;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.request.handler.chat.ChatRequestHandler;
import com.tinatiel.obschatbot.core.request.messaging.ActionRequestGateway;
import com.tinatiel.obschatbot.core.request.messaging.CommandRequestGateway;
import com.tinatiel.obschatbot.core.request.messaging.RequestMessagingConfig;
import com.tinatiel.obschatbot.security.owner.OwnerConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.pircbotx.PircBotX;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * This test verifies that chat action requests make their way to the manager
 */
@EnableIntegration
@ContextConfiguration(classes = {
  TwitchChatClientConfig.class,
  RequestMessagingConfig.class,
  OwnerConfig.class
})
@SpringJUnitConfig
public class ChatActionRequestIT {

  @Autowired
  ActionRequestGateway actionRequestGateway;

  @MockBean
  TwitchChatClientFactory twitchChatClientFactory;

  @MockBean
  ActionCommandConsumer<TwitchChatClientDelegate> twitchChatClientActionCommandConsumer;

  @MockBean
  ChatRequestHandler chatRequestHandler; // don't need
  @MockBean(answer = Answers.RETURNS_DEEP_STUBS)
  OAuth2AuthorizedClientService authorizedClientService; // don't need

  @Autowired
  ClientManager twitchChatClientManager;

  @MockBean
  ClientSettingsFactory<TwitchChatClientSettings> twitchChatClientSettingsFactory;

  @Test
  void whenActionSubmittedThenItIsReceivedByTheConsumer() {

    // Given valid settings are returned (meets org.pircbotx.Configuration.Builder requirements)
    when(twitchChatClientSettingsFactory.getSettings()).thenReturn(
      TwitchChatClientSettings.builder()
        .ircHost("localhost")
        .ircPort(1234)
        .connectionAttempts(3)
        .connectionTimeoutMs(1234)
        .build());

    // And given the clientManager is in a ready state
    twitchChatClientManager.startClient();
    twitchChatClientManager.onLifecycleEvent(new ClientReadyEvent());

    // Given we have an action request for the chat client consumer
    ActionRequest actionRequest = new ActionRequest(
      mock(RequestContext.class), mock(SendMessageAction.class));

    // When we submit it
    actionRequestGateway.submit(actionRequest);

    // Then the consumer receives it
    verify(twitchChatClientActionCommandConsumer).consume(any(), eq(actionRequest));

  }
}
