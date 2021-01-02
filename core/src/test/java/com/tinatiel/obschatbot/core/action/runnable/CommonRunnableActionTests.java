/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action.runnable;

import com.tinatiel.obschatbot.core.action.*;
import com.tinatiel.obschatbot.core.action.model.ObsSourceVisibilityAction;
import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.client.ActionClient;
import com.tinatiel.obschatbot.core.client.ActionClientFactoryImpl;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClient;
import com.tinatiel.obschatbot.core.client.obs.ObsClient;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.client.ActionClientFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

@SuppressWarnings("unchecked")
public class CommonRunnableActionTests {

    ObsClient obsClient;
    TwitchChatClient twitchChatClient;

    RunnableActionFactory runnableActionFactory;

    @BeforeEach
    void setUp() {

        obsClient = mock(ObsClient.class);
        twitchChatClient = mock(TwitchChatClient.class);

        ActionClientFactory actionClientFactory = new ActionClientFactoryImpl(obsClient, twitchChatClient);

        runnableActionFactory = new RunnableActionFactoryImpl(actionClientFactory);

    }

    @ParameterizedTest
    @MethodSource("runnableActionArgs")
    void createRunnableActionAsExpected(Action original, ActionClient client, RequestContext context) {

        // When a runnable action is created from an action
        RunnableAction runnableClone = original.createRunnableAction(client, context);
        System.out.println("original: " + original);
        System.out.println("clone   : " + runnableClone);

        // Then it has the expected properties
        assertThat(runnableClone.getAction()).isEqualToComparingFieldByField(original);
        assertThat(runnableClone.getClient()).isEqualTo(client);
        assertThat(runnableClone.getRequestContext()).isEqualTo(context);

    }

    @ParameterizedTest
    @MethodSource("runnableActionArgs")
    void creatingRunnableClonesWithNullsThrowsExceptions(Action action, ActionClient client, RequestContext context) {

        // When cloned with nulls then throw an exception
        assertThatThrownBy(() -> {
            action.createRunnableAction(null, context);
        }).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> {
            action.createRunnableAction(client, null);
        }).isInstanceOf(IllegalArgumentException.class);

    }

    static Stream<Arguments> runnableActionArgs() {

        RequestContext context = mock(RequestContext.class);
        ObsClient obsClient = mock(ObsClient.class);
        TwitchChatClient twitchChatClient = mock(TwitchChatClient.class);

        return Stream.of(
            Arguments.of(new ObsSourceVisibilityAction("foo", "bar", true), obsClient, context),
            Arguments.of(new SendMessageAction("foo"), twitchChatClient, context)
        );
    }

}
