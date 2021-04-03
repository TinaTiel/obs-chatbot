package com.tinatiel.obschatbot.core.client;

import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClientManager;
import com.tinatiel.obschatbot.core.client.obs.ObsClientManager;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.QueueConfig;
import com.tinatiel.obschatbot.core.request.QueueNotifierConfig;
import com.tinatiel.obschatbot.core.request.RequestContext;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.BlockingQueue;

import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.*;

/**
 * This integration test verifies the client listener is registered with the
 * ActionRequest queue notifier.
 */
@ContextConfiguration(classes = {QueueNotifierConfig.class, ClientConfig.class})
@SpringJUnitConfig
public class ClientListenerIT {

    @Autowired
    BlockingQueue<ActionRequest> actionRequestQueue;

    @MockBean(name = "twitchChatClientManager")
    ClientManager twitchChatClientManager;

    @MockBean(name = "obsClientManager")
    ClientManager obsClientManager;

    @Test
    void twitchChatActionIsRouted() {

        // Given a Twitch Chat request (doesn't matter which)
        ActionRequest actionRequest = new ActionRequest(
                mock(RequestContext.class),
                new SendMessageAction("foo")
        );

        // When sent to the ActionRequest queue
        actionRequestQueue.add(actionRequest);

        // And we wait reasonably
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Then the twitch client manager will have been called
        verify(twitchChatClientManager, times(1)).consume(actionRequest);

    }

    @Disabled
    @Test
    void obsActionRoutedAsExpected() {
        fail("to do");
    }

}
