/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

import com.tinatiel.obschatbot.core.CoreConfig;
import com.tinatiel.obschatbot.core.client.chat.twitch.PircBotxListener;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchClientState;
import com.tinatiel.obschatbot.core.messaging.Listener;
import com.tinatiel.obschatbot.core.messaging.QueueNotifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This test captures the expected 'dance' between the client manager and PircBotX when connecting to Twitch servers,
 * with a mock instance of the bot and manually triggering the expected events.
 * There needs to be a separate continuous test that verifies this with a real bot for the case that Twitch
 * changes how login to their IRC servers changes.
 */
@Import(CoreConfig.class)
@SpringJUnitConfig
public class TwitchChatClientIT {

    private final static String TWITCH_AUTH_FAILED_MESSAGE = "Login authentication failed";

    @Autowired
    QueueNotifier<TwitchClientEvent> notifier;

    TestListener testListener;

    @MockBean
    ClientFactory<PircBotX> clientFactory;

    @Autowired
    PircBotxListener pircBotxListener;

    @Autowired
    ClientManager<TwitchClientEvent> sut;

    @BeforeEach
    void setUp() {

        // Add the test listener so we can monitor events
        testListener = new TestListener();
        notifier.addListener(testListener);
        assertThat(testListener.getLog()).isEmpty();

        // Stub the client factory so it returns fake bots
        PircBotX mockClient = mock(PircBotX.class); // stub bot methods, we're not testing the bot
        when(clientFactory.generate()).thenReturn(mockClient);

    }

    @Test
    void startupWithBadConnection() throws Exception {

        // Given we start the bot
        sut.startClient();

        // Wait
        waitReasonably();

        // But the connection is bad
        pircBotxListener.onConnectAttemptFailed(mock(ConnectAttemptFailedEvent.class));

        // And wait for it to shutdown
        waitReasonably();

        // And it disconnects
        pircBotxListener.onDisconnect(mock(DisconnectEvent.class));

        // And we wait
        waitReasonably();

        // When we examine the log
        List<TwitchClientEvent> events = testListener.getLog();

        // Then we find the expected order of events
        events.forEach(System.out::println);
        assertThat(events.stream().map(TwitchClientEvent::getState)).containsExactly(
                TwitchClientState.START_REQUESTED,
                TwitchClientState.STARTING,
                TwitchClientState.CONNECTING,
                TwitchClientState.ERROR,
                TwitchClientState.STOP_REQUESTED,
                TwitchClientState.STOPPING,
                TwitchClientState.DISCONNECTED,
                TwitchClientState.STOPPED
        );

    }

    @Test
    void startupWithBadCredentials() throws Exception {

        // Given we start the bot
        sut.startClient();

        // Wait
        waitReasonably();

        // Connect to the server
        pircBotxListener.onSocketConnect(mock(SocketConnectEvent.class));

        // wait
        waitReasonably();

        // start auth
        OutputEvent outputEvent = mock(OutputEvent.class);
        when(outputEvent.getRawLine()).thenReturn("PASS oauth:abcd1234");
        pircBotxListener.onOutput(outputEvent);

        // But auth fails
        NoticeEvent noticeEvent = mock(NoticeEvent.class);
        when(noticeEvent.getNotice()).thenReturn(TWITCH_AUTH_FAILED_MESSAGE);
        pircBotxListener.onNotice(noticeEvent);

        // And we wait
        waitReasonably();

        // And it disconnects
        pircBotxListener.onDisconnect(mock(DisconnectEvent.class));

        // And we wait
        waitReasonably();

        // When we examine the log
        List<TwitchClientEvent> events = testListener.getLog();

        // Then we find the expected order of events
        events.forEach(System.out::println);
        assertThat(events.stream().map(TwitchClientEvent::getState)).containsExactly(
                TwitchClientState.START_REQUESTED,
                TwitchClientState.STARTING,
                TwitchClientState.CONNECTING,
                TwitchClientState.CONNECTED,
                TwitchClientState.AUTHENTICATING,
                TwitchClientState.ERROR,
                TwitchClientState.STOP_REQUESTED,
                TwitchClientState.STOPPING,
                TwitchClientState.DISCONNECTED,
                TwitchClientState.STOPPED
        );
    }

    @Test
    void successfulStartupAndShutdown() throws Exception {
        // Given we start the bot
        sut.startClient();

        // Wait
        waitReasonably();

        // Connect to the server
        pircBotxListener.onSocketConnect(mock(SocketConnectEvent.class));

        // wait
        waitReasonably();

        // start auth
        OutputEvent outputEvent = mock(OutputEvent.class);
        when(outputEvent.getRawLine()).thenReturn("PASS oauth:abcd1234");
        pircBotxListener.onOutput(outputEvent);

        // wait
        waitReasonably();

        // auth succeeds
        pircBotxListener.onConnect(mock(ConnectEvent.class));

        // wait
        waitReasonably();

        // join
        pircBotxListener.onJoin(mock(JoinEvent.class, Mockito.RETURNS_DEEP_STUBS));

        // And we wait
        waitReasonably();

        // And then we request shutdown
        sut.stopClient();

        // And we wait
        waitReasonably();

        // And it disconnects
        pircBotxListener.onDisconnect(mock(DisconnectEvent.class));

        // And we wait
        waitReasonably();

        // When we examine the log
        List<TwitchClientEvent> events = testListener.getLog();

        // Then we find the expected order of events
        events.forEach(System.out::println);
        assertThat(events.stream().map(TwitchClientEvent::getState)).containsExactly(
                TwitchClientState.START_REQUESTED,
                TwitchClientState.STARTING,
                TwitchClientState.CONNECTING,
                TwitchClientState.CONNECTED,
                TwitchClientState.AUTHENTICATING,
                TwitchClientState.AUTHENTICATED,
                TwitchClientState.JOINING, // Request Tags capability
                TwitchClientState.JOINING, // Request Command capability
                TwitchClientState.JOINED,
                TwitchClientState.READY,
                TwitchClientState.STOP_REQUESTED,
                TwitchClientState.STOPPING,
                TwitchClientState.DISCONNECTED,
                TwitchClientState.STOPPED
        );
    }

    /**
     * Simple listener that puts each event into a history log so we can
     * view it later and verify the events that executed.
     */
    static class TestListener implements Listener<TwitchClientEvent> {

        List<TwitchClientEvent> log = new ArrayList<>();

        @Override
        public void onEvent(TwitchClientEvent event) {
            log.add(event);
        }

        public List<TwitchClientEvent> getLog() {
            return log;
        }
    }

    private void waitReasonably() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

}
