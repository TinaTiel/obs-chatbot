package com.tinatiel.obschatbot.core.client.twitch;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.core.client.twitch.chat.TwitchChatActionRequestListener;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.RequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TwitchChatActionRequestListenerTest {

    ClientManager clientManager;
    TwitchChatActionRequestListener twitchChatActionRequestListener;

    @BeforeEach
    void setUp() {
        clientManager = mock(ClientManager.class);
        twitchChatActionRequestListener = new TwitchChatActionRequestListener(clientManager);
    }

    @Test
    void twitchChatClientActionsRoutedAsExpected() {

        // Given a mock context
        RequestContext mockContext = mock(RequestContext.class);

        // When we submit requests to the listener
        twitchChatActionRequestListener.onEvent(new ActionRequest(mockContext, mock(SendMessageAction.class)));

        // Then the client manager is invoked
        verify(clientManager, times(1)).consume(any());

    }

    @Test
    void unexpectedRequestsIgnored() {

        // Given a mock context
        RequestContext mockContext = mock(RequestContext.class);

        // When we submit requests to the listener
        twitchChatActionRequestListener.onEvent(new ActionRequest(mockContext, mock(Action.class)));

        // Then the client manager is not invoked
        verify(clientManager, times(0)).consume(any());

    }

}
