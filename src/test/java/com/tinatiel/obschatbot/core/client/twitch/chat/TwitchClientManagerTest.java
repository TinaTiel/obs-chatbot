package com.tinatiel.obschatbot.core.client.twitch.chat;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.client.event.ClientReadyEvent;
import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import com.tinatiel.obschatbot.core.messaging.QueueClient;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.RequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.pircbotx.PircBotX;

import static org.mockito.Mockito.*;

public class TwitchClientManagerTest {

    TwitchClientMessagingGateway stateClient;
    ClientFactory<PircBotX, TwitchChatClientSettings> clientFactory;

    TwitchChatClientManager twitchChatClientManager;

    @BeforeEach
    void setUp() {

        stateClient = mock(TwitchClientMessagingGateway.class);
        clientFactory = mock(ClientFactory.class);

        twitchChatClientManager = new TwitchChatClientManager(stateClient, clientFactory);

    }

    @Test
    void consumeExpectedActions() {

        // Given requests
        RequestContext mockContext = mock(RequestContext.class);
        ActionRequest messageRequest = new ActionRequest(mockContext, mock(SendMessageAction.class));

        // And given factory returns a delgate
        TwitchChatClientDelegate clientDelegate = mock(TwitchChatClientDelegate.class, RETURNS_DEEP_STUBS);
        when(clientFactory.generate()).thenReturn(clientDelegate);

        // And given the clientManager was started
        twitchChatClientManager.startClient();

        // And given the clientManager is in a ready state
        twitchChatClientManager.onEvent(new ClientReadyEvent());

        // When consumed
        twitchChatClientManager.consume(messageRequest);

        // Then the client was invoked
        verify(clientDelegate, times(1)).sendMessage(any());

    }

    @Test
    void ignoreUnexpectedActions() {

        // Given requests
        RequestContext mockContext = mock(RequestContext.class);
        ActionRequest messageRequest = new ActionRequest(mockContext, mock(Action.class));

        // And given factory returns a client
        TwitchChatClientDelegate clientDelegate = mock(TwitchChatClientDelegate.class, RETURNS_DEEP_STUBS);
        when(clientFactory.generate()).thenReturn(clientDelegate);

        // And given the clientManager was started
        twitchChatClientManager.startClient();

        // And given the clientManager is in a ready state
        twitchChatClientManager.onEvent(new ClientReadyEvent());

        // When consumed
        twitchChatClientManager.consume(messageRequest);

        // Then the client was NOT invoked
        verify(clientDelegate, times(0)).sendMessage(any());

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
        twitchChatClientManager.consume(messageRequest);

        // Then the client was NOT invoked
        verify(clientDelegate, times(0)).sendMessage(any());
    }

}
