/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.twitch.chat;

import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.core.client.event.*;
import com.tinatiel.obschatbot.core.messaging.Listener;
import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import com.tinatiel.obschatbot.core.messaging.QueueNotifier;
import com.tinatiel.obschatbot.core.request.QueueNotifierConfig;
import com.tinatiel.obschatbot.core.request.RequestConfig;
import com.tinatiel.obschatbot.core.request.handler.chat.ChatRequestHandler;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.http.impl.conn.Wire;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.channel.interceptor.WireTap;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.test.context.MockIntegrationContext;
import org.springframework.integration.test.context.SpringIntegrationTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.PollableChannel;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This test captures the expected 'dance' between the client manager and PircBotX when connecting to Twitch servers,
 * with a mock instance of the bot and manually triggering the expected events.
 * There needs to be a separate continuous test that verifies this with a real bot for the case that Twitch
 * changes how login to their IRC servers changes.
 */
@SpringIntegrationTest
@ContextConfiguration(classes = {TwitchChatClientConfig.class, TwitchChatClientStateIT.TestConfig.class})
@SpringJUnitConfig
public class TwitchChatClientStateIT {

    private final static String TWITCH_AUTH_FAILED_MESSAGE = "Login authentication failed";

    @Autowired
    QueueChannel testChannel;

    @Autowired
    Queue<Message<?>> testChannelQueue;

    @Qualifier("twitchClientLifecycleChannel")
    @Autowired
    AbstractMessageChannel targetChannel;

    @EnableIntegration
    @TestConfiguration
    public static class TestConfig {

        @Bean
        Queue<Message<?>> testChannelQueue() {
            return new LinkedBlockingQueue<>();
        }

        @Bean
        QueueChannel testChannel() {
            return new QueueChannel(testChannelQueue());
        }

    }

    @MockBean
    ClientFactory<PircBotX, TwitchChatClientSettings> clientFactory;

    @Autowired
    PircBotxListener pircBotxListener;

    @Autowired
    ClientManager twitchChatClientManager;

    @MockBean
    ChatRequestHandler chatRequestHandler;

    @MockBean
    OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    @BeforeEach
    void setUp() {

        // Intercept messages from the lifecycle channel
        targetChannel.addInterceptor(new WireTap(testChannel));

        // Stub the client factory so it returns fake bots
        PircBotX mockClient = mock(PircBotX.class); // stub bot methods, we're not testing the bot
        TwitchChatClientSettings settings = mock(TwitchChatClientSettings.class);
        when(clientFactory.generate()).thenReturn(
                new TwitchChatClientDelegate(mockClient, settings));

    }

    @AfterEach
    void tearDown() {
        // Clear the channel and remove the interceptor
        testChannel.clear();
        targetChannel.removeInterceptor(0);
    }

    @Test
    void startupWithBadConnection() throws Exception {

        // Given we start the bot
        twitchChatClientManager.startClient();

        // Wait
        waitReasonably();

        // But the connection is bad
        pircBotxListener.onConnectAttemptFailed(mock(ConnectAttemptFailedEvent.class, Mockito.RETURNS_DEEP_STUBS));

        // And wait for it to shutdown
        waitReasonably();

        // And it disconnects
        pircBotxListener.onDisconnect(mock(DisconnectEvent.class));

        // And we wait
        waitReasonably();

        // When we examine the log
        List<ObsChatbotEvent> events = testChannelQueue.stream()
          .map(Message::getPayload)
          .map(ObsChatbotEvent.class::cast)
          .collect(Collectors.toList());
        events.forEach(System.out::println);

        // Then we find the expected order of events
        List<Class> eventClasses = events.stream()
                .map(ObsChatbotEvent::getClass)
                .collect(Collectors.toList());
        assertThat(eventClasses
        ).containsExactly(
                ClientStartRequestedEvent.class,
                ClientStartingEvent.class,
                ClientConnectingEvent.class,
                ClientErrorEvent.class,
                ClientStopRequestedEvent.class,
                ClientStoppingEvent.class,
                ClientDisconnectedEvent.class,
                ClientStoppedEvent.class
        );

    }

    @Test
    void startupWithBadCredentials() throws Exception {

        // Given we start the bot
        twitchChatClientManager.startClient();

        // Wait for it to start
        waitReasonably();

        // Then connect to the server
        pircBotxListener.onSocketConnect(mock(SocketConnectEvent.class));

        // wait for connection to finish
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

        // And we wait for disconnection
        waitReasonably();

        // When we examine the log
        List<ObsChatbotEvent> events = testChannelQueue.stream()
          .map(Message::getPayload)
          .map(ObsChatbotEvent.class::cast)
          .collect(Collectors.toList());
        events.forEach(System.out::println);

        // Then we find the expected order of events
        List<Class> eventClasses = events.stream()
                .map(ObsChatbotEvent::getClass)
                .collect(Collectors.toList());
        assertThat(eventClasses).containsExactly(
                ClientStartRequestedEvent.class,
                ClientStartingEvent.class,
                ClientConnectingEvent.class,
                ClientConnectedEvent.class,
                ClientAuthenticatingEvent.class,
                ClientErrorEvent.class,
                ClientStopRequestedEvent.class,
                ClientStoppingEvent.class,
                ClientDisconnectedEvent.class,
                ClientStoppedEvent.class
        );
    }

    @Test
    void successfulStartupAndShutdown() throws Exception {

        // Given we start the bot
        twitchChatClientManager.startClient();

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
        twitchChatClientManager.stopClient();

        // And we wait
        waitReasonably();

        // And it disconnects
        pircBotxListener.onDisconnect(mock(DisconnectEvent.class));

        // And we wait
        waitReasonably();

        // When we examine the log
        List<ObsChatbotEvent> events = testChannelQueue.stream()
          .map(Message::getPayload)
          .map(ObsChatbotEvent.class::cast)
          .collect(Collectors.toList());
        events.forEach(System.out::println);

        // Then we find the expected order of events
        List<Class> eventClasses = events.stream()
                .map(ObsChatbotEvent::getClass)
                .collect(Collectors.toList());
        assertThat(eventClasses).containsExactly(
                ClientStartRequestedEvent.class,
                ClientStartingEvent.class,
                ClientConnectingEvent.class,
                ClientConnectedEvent.class,
                ClientAuthenticatingEvent.class,
                ClientAuthenticatedEvent.class,
                ClientJoiningEvent.class, // Request Tags capability
                ClientJoiningEvent.class, // Request Command capability
                ClientJoinedEvent.class,
                ClientReadyEvent.class,
                ClientStopRequestedEvent.class,
                ClientStoppingEvent.class,
                ClientDisconnectedEvent.class,
                ClientStoppedEvent.class
        );
    }

    private void waitReasonably() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

}
