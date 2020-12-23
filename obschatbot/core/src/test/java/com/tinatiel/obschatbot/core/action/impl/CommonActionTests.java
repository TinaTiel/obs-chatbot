package com.tinatiel.obschatbot.core.action.impl;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.ActionContext;
import com.tinatiel.obschatbot.core.action.ActionType;
import com.tinatiel.obschatbot.core.actionservice.ActionServiceFactory;
import com.tinatiel.obschatbot.core.command.Command;
import org.junit.jupiter.api.Test;
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

        ActionContext context = mock(ActionContext.class);

        Action clone = action.createRunnableClone(action, context);
        assertThat(clone).isEqualToIgnoringGivenFields(context, "actionContext");
        assertThat(clone.getActionContext()).isEqualTo(context);

    }

    static Stream<Arguments> actionTypes() {

        ActionServiceFactory factory = mock(ActionServiceFactory.class);

        return Stream.of(
                Arguments.of(ActionType.SYSTEM, new ExecuteCommandAction(mock(Command.class))),
                Arguments.of(ActionType.OBS, new ObsSourceVisibilityAction(factory, null, "foo", "bar", true))
        );

    }

}
