/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action.runnable;

import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClient;
import com.tinatiel.obschatbot.core.request.RequestContext;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Disabled
class RunnableSendMessageActionTest {

    @Test
    void runInvokesMessageClient() {

        // Given a message client
        TwitchChatClient chatClient = mock(TwitchChatClient.class);

        // Given a RunnableSendMessageAction
        RunnableSendMessageAction action = new RunnableSendMessageAction(
                mock(SendMessageAction.class),
                chatClient,
                mock(RequestContext.class)
        );

        // When run
        action.run();

        // Then the message client is called with the expected message
        verify(chatClient).sendMessage(any());

    }

    // TODO add tests for message variables here, eventually?

}