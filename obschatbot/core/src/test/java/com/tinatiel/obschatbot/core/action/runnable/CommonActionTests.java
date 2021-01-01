/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action.runnable;

import com.tinatiel.obschatbot.core.action.*;
import com.tinatiel.obschatbot.core.action.model.ExecuteCommandAction;
import com.tinatiel.obschatbot.core.action.model.ObsSourceVisibilityAction;
import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.client.ActionClient;
import com.tinatiel.obschatbot.core.client.ActionClientFactoryImpl;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClient;
import com.tinatiel.obschatbot.core.client.obs.ObsClient;
import com.tinatiel.obschatbot.core.dispatch.CommandRequestContext;
import com.tinatiel.obschatbot.core.client.ActionClientFactory;
import com.tinatiel.obschatbot.core.command.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class CommonActionTests {

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
    @MethodSource("actionTypes")
    void actionTypeAsExpected(ActionType expected, Action action) {

        // For each action, assert that it has the expected actionType
        assertThat(action.getActionType()).isEqualTo(expected);

    }

    @ParameterizedTest
    @MethodSource("actionTypes")
    void cloneAsExpected(ActionType ignored, Action action) {

        // For each action, assert that its clone is identical
        assertThat(action.clone()).isEqualToComparingFieldByField(action);

    }

    @ParameterizedTest
    @MethodSource("runnableActions")
    void createRunnableCloneAsExpected(Action action, ActionClient client) {

        // Given a request context
        CommandRequestContext context = mock(CommandRequestContext.class);

        // When an action is created as a runnable clone
        RunnableAction clone = action.createRunnableAction(client, context);
        System.out.println("original: " + action);
        System.out.println("clone   : " + clone);

        // Then it has the expected properties
        assertThat(clone.getAction()).isEqualToComparingFieldByField(action);
        assertThat(clone.getClient()).isEqualTo(client);
        assertThat(clone.getRequestContext()).isEqualTo(context);

//        // And attempts to create with the wrong client throw an exception
//        assertThatThrownBy(() -> {
//            action.createRunnableAction(mock(ActionClient.class), context);
//        }).isInstanceOf(IllegalArgumentException.class);

    }

//    @ParameterizedTest
//    @MethodSource("runnableActions")
//    void createRunnableCloneWithWrongClient(Action action, ActionClient client) {
//
//        // Given a request context
//        CommandRequestContext context = mock(CommandRequestContext.class);
//
//        // When cloned with the wrong client throw an exception
//        assertThatThrownBy(() -> {
//            action.createRunnableAction(mock(ActionClient.class), context);
//        }).isInstanceOf(IllegalArgumentException.class);
//
//    }

    @ParameterizedTest
    @MethodSource("runnableActions")
    void createRunnableCloneNulls(Action action, ActionClient client) {

        // Given a request context
        CommandRequestContext context = mock(CommandRequestContext.class);

        // When cloned with nulls then throw an exception
        assertThatThrownBy(() -> {
            action.createRunnableAction(null, context);
        }).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> {
            action.createRunnableAction(client, null);
        }).isInstanceOf(IllegalArgumentException.class);

    }

    static Stream<Arguments> actionTypes() {

        return Stream.of(
                Arguments.of(ActionType.SYSTEM, new ExecuteCommandAction(mock(Command.class))),
                Arguments.of(ActionType.OBS, new ObsSourceVisibilityAction( "foo", "bar", true)),
                Arguments.of(ActionType.TWITCH_CHAT, new SendMessageAction("foo"))
        );

    }

    static Stream<Arguments> runnableActions() {

        ObsClient obsClient = mock(ObsClient.class);
        TwitchChatClient twitchChatClient = mock(TwitchChatClient.class);

        return Stream.of(
            Arguments.of(new ObsSourceVisibilityAction("foo", "bar", true), obsClient),
            Arguments.of(new SendMessageAction("foo"), twitchChatClient)
        );
    }

}
