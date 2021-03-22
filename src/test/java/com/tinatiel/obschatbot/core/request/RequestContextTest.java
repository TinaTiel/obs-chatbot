/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request;

import com.tinatiel.obschatbot.core.user.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class RequestContextTest {

    @Test
    void userAndArgsAreRequired() {
        assertThatThrownBy(() -> {
            new RequestContext(null, new ArrayList<>());
        }).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> {
            new RequestContext(mock(User.class), null);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void contextUniqueByUser() {

        // Given an user and command
        User user1 = mock(User.class);

        // Given contexts from the same user, everything else the same
        RequestContext context1 = new RequestContext(user1, new ArrayList<>());
        RequestContext context1Copy = new RequestContext(user1, new ArrayList<>());

        // Then they are equal
        assertThat(context1)
                .isNotSameAs(context1Copy)
                .isEqualTo(context1Copy);

        // But given context from different user
        User user2 = mock(User.class);
        RequestContext context2 = new RequestContext(user2, new ArrayList<>());

        // Then they are not equal
        assertThat(context2).isNotEqualTo(context1);

    }

    @Test
    void contextUniqueByArgs() {

        // Given an user and command
        User user = mock(User.class);

        // Given contexts from the same user, everything else the same
        RequestContext context1 = new RequestContext(user, Arrays.asList("arg1", "arg2", "arg3"));
        RequestContext context1Copy = new RequestContext(user, Arrays.asList("arg1", "arg2", "arg3"));

        // Then they are equal
        assertThat(context1)
                .isNotSameAs(context1Copy)
                .isEqualTo(context1Copy);

        // But given context from same user with different args (order matters)
        RequestContext context2 = new RequestContext(user, Arrays.asList("arg3", "arg2", "arg1"));

        // Then they are not equal
        assertThat(context2).isNotEqualTo(context1);

    }

}
