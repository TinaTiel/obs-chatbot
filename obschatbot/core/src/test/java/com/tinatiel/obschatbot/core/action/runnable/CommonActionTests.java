/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action.runnable;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.RunnableAction;
import com.tinatiel.obschatbot.core.action.model.ExecuteCommandAction;
import com.tinatiel.obschatbot.core.action.model.ObsSourceVisibilityAction;
import com.tinatiel.obschatbot.core.dispatch.CommandRequestContext;
import com.tinatiel.obschatbot.core.action.ActionType;
import com.tinatiel.obschatbot.core.client.ActionClientFactory;
import com.tinatiel.obschatbot.core.command.Command;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class CommonActionTests {

    @ParameterizedTest
    @MethodSource("actionTypes")
    void actionTypeAsExpected(ActionType expected, Action action) {

        // For each action, assert that it has the expected actionType
        assertThat(action.getActionType()).isEqualTo(expected);

    }

//    @ParameterizedTest
//    @MethodSource("actionTypes")
//    void cloneAsExpected(ActionType expected, RunnableAction action) {
//
//        // Given a request context
//        CommandRequestContext context = mock(CommandRequestContext.class);
//
//        // When an action is created as a runnable clone
//        RunnableAction clone = action.createRunnableClone(context);
//        System.out.println("original: " + action);
//        System.out.println("clone   : " + clone);
//
//        // Then it has the same fields as the original
//        assertThat(clone).isEqualToIgnoringGivenFields(action, "commandRequestContext");
//
//        // And has the expected context
//        assertThat(clone.getRequestContext()).isEqualTo(context);
//
//    }

    static Stream<Arguments> actionTypes() {

        ActionClientFactory factory = mock(ActionClientFactory.class);

        return Stream.of(
                Arguments.of(ActionType.SYSTEM, new ExecuteCommandAction(mock(Command.class))),
                Arguments.of(ActionType.OBS, new ObsSourceVisibilityAction( "foo", "bar", true))
        );

    }

}
