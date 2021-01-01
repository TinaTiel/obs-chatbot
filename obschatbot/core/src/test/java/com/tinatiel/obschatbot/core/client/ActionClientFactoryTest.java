/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

import com.tinatiel.obschatbot.core.action.ActionType;
import com.tinatiel.obschatbot.core.client.obs.ObsClient;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClient;
import com.tinatiel.obschatbot.core.error.ClientNotRegisteredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class ActionClientFactoryTest {

    ObsClient obsClient;
    TwitchChatClient twitchChatClient;

    ActionClientFactory factory;

    @BeforeEach
    void setUp() {

        obsClient = mock(ObsClient.class);
        twitchChatClient = mock(TwitchChatClient.class);

        factory = new ActionClientFactoryImpl(obsClient, twitchChatClient);

    }

    @Test
    void nullArgThrowsException() {

        assertThatThrownBy(() -> {
            factory.getService(null);
        }).isInstanceOf(IllegalArgumentException.class);

    }


    @Test
    void returnObsClient() {
        assertThat(factory.getService(ActionType.OBS))
                .isInstanceOf(ObsClient.class)
                .isEqualTo(obsClient);
    }

    @Test
    void returnTwitchChatClient() {
        assertThat(factory.getService(ActionType.TWITCH_CHAT))
                .isInstanceOf(TwitchChatClient.class)
                .isEqualTo(twitchChatClient);
    }

    @ParameterizedTest
    @MethodSource("expectedClients")
    void serviceReturnsExpectedClients(Class<? extends ActionClient> request) {
        assertThat(factory.getClient(request))
                .isNotNull()
                .isInstanceOf(request);
    }

    @Test
    void unregisteredClientThrowsException() {

        // Given a client class we know that isn't registered
        Class<? extends ActionClient> clientClass = UnregisteredClient.class;

        // When requested, then an exception will be thrown
        assertThatThrownBy(() -> {
            factory.getClient(clientClass);
        }).isInstanceOf(ClientNotRegisteredException.class);
    }

    private static class UnregisteredClient implements ActionClient {

        @Override
        public ActionType getActionType() {
            return null;
        }
    }

    static Stream<Arguments> expectedClients() {
        return Stream.of(
                Arguments.of(ObsClient.class),
                Arguments.of(TwitchChatClient.class)
        );
    }

}
