package com.tinatiel.obschatbot.core.client.twitch.chat;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.client.ActionCommandConsumer;
import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.client.event.ClientReadyEvent;
import com.tinatiel.obschatbot.core.client.event.ClientRequestIgnoredEvent;
import com.tinatiel.obschatbot.core.client.twitch.chat.messaging.TwitchClientLifecycleGateway;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.RequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pircbotx.PircBotX;

import static org.mockito.Mockito.*;

public class TwitchClientManagerTest {

    TwitchClientLifecycleGateway stateClient;
    ClientFactory<PircBotX, TwitchChatClientSettings> clientFactory;
    ActionCommandConsumer<TwitchChatClientDelegate> twitchChatClientActionCommandConsumer;

    TwitchChatClientManager twitchChatClientManager;

    @BeforeEach
    void setUp() {

        stateClient = mock(TwitchClientLifecycleGateway.class);
        clientFactory = mock(ClientFactory.class);
        twitchChatClientActionCommandConsumer = mock(ActionCommandConsumer.class);

        twitchChatClientManager = new TwitchChatClientManager(
          stateClient, clientFactory, twitchChatClientActionCommandConsumer);

    }

    @Test
    void consumeExpectedActionsWhenReady() {

        // Given requests, doesn't matter what they are
        RequestContext mockContext = mock(RequestContext.class);
        ActionRequest messageRequest = new ActionRequest(mockContext, mock(Action.class));

        // And given factory returns a delgate
        TwitchChatClientDelegate clientDelegate = mock(TwitchChatClientDelegate.class, RETURNS_DEEP_STUBS);
        when(clientFactory.generate()).thenReturn(clientDelegate);

        // And given the clientManager is in a ready state
        twitchChatClientManager.startClient();
        twitchChatClientManager.onLifecycleEvent(new ClientReadyEvent());

        // When consumed
        twitchChatClientManager.onActionRequest(messageRequest);

        // Then the client was invoked
        verify(twitchChatClientActionCommandConsumer, times(1))
          .consume(any(), any());

    }

    @Test
    void ignoreActionsWhenNotReady() {

        // Given requests
        RequestContext mockContext = mock(RequestContext.class);
        ActionRequest messageRequest = new ActionRequest(mockContext, mock(Action.class));

        // And given factory returns a client
        TwitchChatClientDelegate clientDelegate = mock(TwitchChatClientDelegate.class);
        when(clientFactory.generate()).thenReturn(clientDelegate);

        // And given the clientManager was started
        twitchChatClientManager.startClient();

        // (but is not in a ready state)

        // When consumed
        twitchChatClientManager.onActionRequest(messageRequest);

        // Then the client was NOT invoked
        verify(clientDelegate, times(0)).sendMessage(any());
    }

    @Test
    void consumeActionsWhenReadyDespiteIgnoredStarts() {

        // Given requests
        RequestContext mockContext = mock(RequestContext.class);
        ActionRequest messageRequest = new ActionRequest(mockContext, mock(Action.class));

        // And given factory returns a delgate
        TwitchChatClientDelegate clientDelegate = mock(TwitchChatClientDelegate.class, RETURNS_DEEP_STUBS);
        when(clientFactory.generate()).thenReturn(clientDelegate);

        // Given the client manager is ready to accept requests
        twitchChatClientManager.startClient();
        twitchChatClientManager.onLifecycleEvent(new ClientReadyEvent());

        // But it receives another start request
        twitchChatClientManager.startClient();
        twitchChatClientManager.onLifecycleEvent(new ClientRequestIgnoredEvent(""));

        // When consumed
        twitchChatClientManager.onActionRequest(messageRequest);

        // Then it still invokes the client
        verify(twitchChatClientActionCommandConsumer, times(1))
          .consume(any(), any());

    }
}
