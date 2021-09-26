/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action.model;

import com.tinatiel.obschatbot.core.action.Action;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class CommonActionTests {

    @ParameterizedTest
    @MethodSource("actions")
    void cloneAsExpected(Action original) {

        // When an action is cloned
        Action clone = original.clone();

        // Then it is identical to the original
        assertThat(clone).isEqualToComparingFieldByField(original);

    }

    static Stream<Arguments> actions() {
        return Stream.of(
                Arguments.of(new ObsSourceVisibilityAction("foo", "bar", true)),
                Arguments.of(new SendMessageAction("foo"))
        );
    }

}
