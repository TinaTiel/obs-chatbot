/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obs.chatbot.core.client;

import com.tinatiel.obschatbot.App;
import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;
import com.tinatiel.obschatbot.core.client.Listener;
import com.tinatiel.obschatbot.core.client.chat.twitch.ClientManagerTwitchChatImpl;
import com.tinatiel.obschatbot.core.client.chat.twitch.PircBotxListener;
import com.tinatiel.obschatbot.core.error.ClientException;
import com.tinatiel.obschatbot.core.error.Code;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.events.NoticeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

@Import({App.class})
@ExtendWith(SpringExtension.class)
public class TwitchClientManagerIT {

    @Autowired
    ClientManager<PircBotX> clientManager;

    @Autowired
    ClientSettingsFactory clientSettingsFactory;

    @MockBean
    ClientFactory<PircBotX> clientFactory;

    @Autowired
    Listener twitchChatListener;

    PircBotX pircBotX;

//    @BeforeEach
//    void setUp() {
//        pircBotX = mock(PircBotX.class);
//        when(clientFactory.generate()).thenReturn(pircBotX);
//        clientManager = new ClientManagerTwitchChatImpl(clientSettingsFactory, clientFactory, twitchChatListener);
//    }
//
//    @Test
//    void whenBadHostThenWrapInHelpfulClientException() throws IOException, IrcException {
//
//        // Given connect throws an UnknownHost exception
//        doThrow(new UnknownHostException()).when(pircBotX).startBot();
//
//        // When started then a client exception is thrown
//        Throwable thrown = catchThrowable(() -> {
//            clientManager.startClient();
//        });
//        assertThat(thrown)
//                .isInstanceOf(ClientException.class)
//                .hasCauseInstanceOf(UnknownHostException.class)
//                .hasMessageContaining("host");
//        assertThat(((ClientException) thrown).getCode()).isEqualTo(Code.CLIENT_UNREACHABLE);
//
//    }
//
//    @Test
//    void whenCannotBeReachedThenWrapInClientException() throws IOException, IrcException {
//
//        // Given connect throws a Connect exception
//        doThrow(new ConnectException()).when(pircBotX).startBot();
//
//        // When started then a client exception is thrown
//        Throwable thrown = catchThrowable(() -> {
//            clientManager.startClient();
//        });
//        assertThat(thrown)
//                .isInstanceOf(ClientException.class)
//                .hasCauseInstanceOf(ConnectException.class)
//                .hasMessageContaining("network");
//        assertThat(((ClientException) thrown).getCode()).isEqualTo(Code.CLIENT_UNREACHABLE);
//    }
//
//    @Test
//    void whenBadAuthThenThrowClientException() {
//
//        // Given bad auth; e.g. specific NOTICE event emitted by IRC; TODO: Continuous test to verify this stays true
//        PircBotxListener listener = new PircBotxListener(null, null);
//        NoticeEvent badAuthNoticeEvent = mock(NoticeEvent.class);
//        when(badAuthNoticeEvent.getNotice()).thenReturn("Login authentication failed");
//
//        // When notice emitted, then a ClientException is thrown
//        assertThatThrownBy(() -> {
//            listener.onNotice(badAuthNoticeEvent);
//        }).isInstanceOf(ClientException.class)
//                .hasMessageContaining("bad credentials");
//
//        // Then...
//
//    }

}
