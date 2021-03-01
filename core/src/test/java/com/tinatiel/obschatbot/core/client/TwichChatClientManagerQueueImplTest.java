/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

import com.tinatiel.obschatbot.core.client.chat.twitch.Listener;
import com.tinatiel.obschatbot.core.client.chat.twitch.State;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchStateMessage;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClientManagerQueueImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pircbotx.PircBotX;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TwichChatClientManagerQueueImplTest {

    BlockingQueue<TwitchStateMessage> stateMessages;
    ClientFactory<PircBotX> clientFactory;
    Listener<PircBotX> listener;

    TwitchChatClientManagerQueueImpl chatClient;

    @BeforeEach
    void setUp() {
        stateMessages = new LinkedBlockingQueue<>();
        clientFactory = mock(ClientFactory.class);
        listener = mock(Listener.class);

        chatClient = new TwitchChatClientManagerQueueImpl(stateMessages, clientFactory, listener);
    }

    @Test
    void lastMessageSentToQueueIsCurrentState() {

        // Given a queue with several messages
        PircBotX clientInstance = mock(PircBotX.class);
        stateMessages.add(new TwitchStateMessage(clientInstance, State.STOPPED, ""));
        stateMessages.add(new TwitchStateMessage(clientInstance, State.STARTING, ""));
        stateMessages.add(new TwitchStateMessage(clientInstance, State.CONNECTED, ""));

        // When the state is queried after a reasonable delay
        try {
            Thread.sleep(20);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        System.out.println("hello");

        State currentState = chatClient.getState();
        System.out.println("there");

        // Then it is eventually-consistent; it reflects the last message in the queue
        assertThat(currentState).isEqualTo(State.CONNECTED);
        verify(listener, times(3)).onState(any());

    }
}
