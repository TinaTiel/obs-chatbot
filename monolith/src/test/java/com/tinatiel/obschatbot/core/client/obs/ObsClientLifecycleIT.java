/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.obs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tinatiel.obschatbot.core.SpringIntegrationTestConfig;
import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.core.client.event.ClientAuthenticatedEvent;
import com.tinatiel.obschatbot.core.client.event.ClientAuthenticatingEvent;
import com.tinatiel.obschatbot.core.client.event.ClientConnectedEvent;
import com.tinatiel.obschatbot.core.client.event.ClientConnectingEvent;
import com.tinatiel.obschatbot.core.client.event.ClientDisconnectedEvent;
import com.tinatiel.obschatbot.core.client.event.ClientErrorEvent;
import com.tinatiel.obschatbot.core.client.event.ClientJoinedEvent;
import com.tinatiel.obschatbot.core.client.event.ClientJoiningEvent;
import com.tinatiel.obschatbot.core.client.event.ClientReadyEvent;
import com.tinatiel.obschatbot.core.client.event.ClientStartRequestedEvent;
import com.tinatiel.obschatbot.core.client.event.ClientStartingEvent;
import com.tinatiel.obschatbot.core.client.event.ClientStopRequestedEvent;
import com.tinatiel.obschatbot.core.client.event.ClientStoppedEvent;
import com.tinatiel.obschatbot.core.client.event.ClientStoppingEvent;
import com.tinatiel.obschatbot.core.client.twitch.chat.PircBotxListener;
import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import net.twasi.obsremotejava.OBSRemoteController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.OutputEvent;
import org.pircbotx.hooks.events.SocketConnectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.channel.interceptor.WireTap;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.Message;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * This tests the 'dance' of events emitted between the client manager and the OBS RemoteController
 * instance, verifying the expected event sequence is generated given the OBS client behaves in
 * specific ways (that should be validated externally).
 */
@Disabled   // We literally cannot test the OBS client right now because it provides no handlers
            // to manually kickoff any lifecycle events on its side. TODO: contribute to fix this
@EnableIntegration
@ContextConfiguration(classes = {ObsClientConfig.class, SpringIntegrationTestConfig.class})
@SpringJUnitConfig
public class ObsClientLifecycleIT {

    @Autowired
    QueueChannel testChannel;

    @Autowired
    Queue<Message<?>> testChannelQueue;

    @Qualifier("obsClientLifecycleChannel")
    @Autowired
    AbstractMessageChannel targetChannel;

    @MockBean
    ClientFactory<OBSRemoteController, ObsClientSettings> clientFactory;

    @Qualifier("obsClientManager")
    @Autowired
    ClientManager clientManager;

    OBSRemoteController mockClient;
    ObsClientSettings settings;

    @BeforeEach
    void setUp() {

        // Intercept messages from the lifecycle channel
        targetChannel.addInterceptor(new WireTap(testChannel));

        // Stub the client factory so it returns fake bots
        mockClient = mock(OBSRemoteController.class); // stub bot methods, we're not testing the bot
        settings = mock(ObsClientSettings.class);
        when(clientFactory.generate()).thenReturn(
                new ObsClientDelegate(mockClient, settings));

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
        clientManager.startClient();

        // Wait
        waitReasonably();

        // But the connection is bad
        // TODO contribute to client oss, expose onXXX() events
        clientManager.onLifecycleEvent(new ClientErrorEvent("Websocket error occurred with session null"));

        // And wait for it to shutdown
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

        // Given we have credentials
        when(settings.getPassword()).thenReturn("somepassword");

        // Given we start the bot
        clientManager.startClient();

        // Wait for it to start & connect
        waitReasonably();

        // But auth fails
        clientManager.onLifecycleEvent(new ClientErrorEvent(
          "Connection to OBS failed: Failed to authenticate with password. Error: Authentication Failed."
        ));

        // And we wait
        waitReasonably();

        // And it disconnects
        clientManager.onLifecycleEvent(new ClientDisconnectedEvent(
          "OBS Client closed with statusCode 1006 and reason 'Disconnected'"
        ));

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

        // Given we have credentials
        when(settings.getPassword()).thenReturn("somepassword");

        // Given we start the bot
        clientManager.startClient();

        // Wait
        waitReasonably();

        // auth succeeds
        // ???

        // And we wait
        waitReasonably();

        // And then we request shutdown
        clientManager.stopClient();

        // And we wait
        waitReasonably();

        // And it disconnects
//        pircBotxListener.onDisconnect(mock(DisconnectEvent.class));

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
                ClientAuthenticatingEvent.class,
                ClientAuthenticatedEvent.class,
                ClientConnectedEvent.class,
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
