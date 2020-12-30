/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action.impl;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.dispatch.CommandRequest;
import com.tinatiel.obschatbot.core.action.ActionType;
import com.tinatiel.obschatbot.core.actionservice.ActionServiceFactory;
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

        assertThat(action.getActionType()).isEqualTo(expected);

    }

    @ParameterizedTest
    @MethodSource("actionTypes")
    void cloneAsExpected(ActionType expected, Action action) {

        CommandRequest context = mock(CommandRequest.class);

        Action clone = action.createRunnableClone(context);
        System.out.println("original: " + action);
        System.out.println("clone   : " + clone);
        assertThat(clone).isEqualToIgnoringGivenFields(action, "commandRequest");
        assertThat(clone.getActionContext()).isEqualTo(context);

    }

    static Stream<Arguments> actionTypes() {

        ActionServiceFactory factory = mock(ActionServiceFactory.class);

        return Stream.of(
                Arguments.of(ActionType.SYSTEM, new ExecuteCommandAction(null, mock(Command.class))),
                Arguments.of(ActionType.OBS, new ObsSourceVisibilityAction(null, factory, "foo", "bar", true))
        );

    }

}
