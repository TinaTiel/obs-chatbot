/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClientManager;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClientManagerImpl;
import com.tinatiel.obschatbot.core.error.ClientNotAvailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class TwitchClientManagerTest {

    TwitchChatClientManager clientManager;

    ClientFactory<PircBotX> clientFactory;
    ClientSettingsFactory clientSettingsFactory;

    @BeforeEach
    void setUp() {
        clientFactory = mock(ClientFactory.class);
        clientSettingsFactory = mock(ClientSettingsFactory.class);
        clientManager = new TwitchChatClientManagerImpl(clientFactory, clientSettingsFactory);
    }

    @Test
    void whenBadHostThenWrapInHelpfulClientException() throws IOException, IrcException {

        // Given connect throws an UnknownHost exception
        PircBotX pircBotX = mock(PircBotX.class);
        doThrow(new UnknownHostException()).when(pircBotX).startBot();
        when(clientFactory.generate(any(), any())).thenReturn(pircBotX);

        // When started then a client exception is thrown
        assertThatThrownBy(() -> {
            clientManager.start();
        }).isInstanceOf(ClientNotAvailableException.class)
                .hasCauseInstanceOf(UnknownHostException.class)
                .hasMessageMatching("wrong host");

    }

    @Test
    void whenHostNotReachableThenWrapInClientException() throws IOException, IrcException {

        // Given connect throws a Connect exception
        PircBotX pircBotX = mock(PircBotX.class);
        doThrow(new ConnectException()).when(pircBotX).startBot();
        when(clientFactory.generate(any(), any())).thenReturn(pircBotX);

        // When started then a client exception is thrown
        assertThatThrownBy(() -> {
            clientManager.start();
        }).isInstanceOf(ClientNotAvailableException.class)
                .hasCauseInstanceOf(UnknownHostException.class)
                .hasMessageMatching("could not reach");
    }

    @Test
    void whenBadAuthThenThrowClientException() {

        // When we connect

        // But bad auth; e.g. specific NOTICE event emitted by IRC

        // Then a helpful exception should be thrown

    }

}
