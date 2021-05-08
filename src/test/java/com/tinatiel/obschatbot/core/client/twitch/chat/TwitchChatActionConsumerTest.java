package com.tinatiel.obschatbot.core.client.twitch.chat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.client.event.ClientReadyEvent;
import com.tinatiel.obschatbot.core.client.twitch.chat.messaging.TwitchClientStateMessagingGateway;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.RequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TwitchChatActionConsumerTest {

  TwitchClientStateMessagingGateway gateway;
  TwitchChatActionCommandConsumer consumer;

  @BeforeEach
  void setUp() {
    gateway = mock(TwitchClientStateMessagingGateway.class);
    consumer = new TwitchChatActionCommandConsumer(gateway);
  }

  @Test
  void ignoreUnexpectedActions() {

    // Given request that has an unknown action type
    RequestContext mockContext = mock(RequestContext.class);
    ActionRequest request = new ActionRequest(mockContext, mock(Action.class));

    // Given we have a client instance
    TwitchChatClientDelegate client = mock(TwitchChatClientDelegate.class);

    // When consumed
    consumer.consume(client, request);

    // Then the client was NOT invoked
    verifyNoInteractions(client);

  }

  @Test
  void sendMessageAction() {

    // Given request to send a message
    RequestContext mockContext = mock(RequestContext.class);
    ActionRequest request = new ActionRequest(mockContext, mock(SendMessageAction.class));

    // Given we have a client instance
    TwitchChatClientDelegate client = mock(TwitchChatClientDelegate.class);

    // When consumed
    consumer.consume(client, request);

    // Then the client is invoked
    verify(client).sendMessage(any());

  }
}
