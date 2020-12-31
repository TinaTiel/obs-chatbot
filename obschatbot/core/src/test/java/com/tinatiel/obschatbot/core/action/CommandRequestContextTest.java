/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action;

import com.tinatiel.obschatbot.core.dispatch.CommandRequestContext;
import com.tinatiel.obschatbot.core.user.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class CommandRequestContextTest {

    @Test
    void contextUniqueByUser() {

        // Given an user and command
        User user1 = mock(User.class);

        // Given contexts from the same user, everything else the same
        CommandRequestContext context1 = new CommandRequestContext(user1, new ArrayList<>());
        CommandRequestContext context1Copy = new CommandRequestContext(user1, new ArrayList<>());

        // Then they are equal
        assertThat(context1)
                .isNotSameAs(context1Copy)
                .isEqualTo(context1Copy);

        // But given context from different user
        User user2 = mock(User.class);
        CommandRequestContext context2 = new CommandRequestContext(user2, new ArrayList<>());

        // Then they are not equal
        assertThat(context2).isNotEqualTo(context1);

    }

    @Test
    void contextUniqueByArgs() {

        // Given an user and command
        User user = mock(User.class);

        // Given contexts from the same user, everything else the same
        CommandRequestContext context1 = new CommandRequestContext(user, Arrays.asList("arg1", "arg2", "arg3"));
        CommandRequestContext context1Copy = new CommandRequestContext(user, Arrays.asList("arg1", "arg2", "arg3"));

        // Then they are equal
        assertThat(context1)
                .isNotSameAs(context1Copy)
                .isEqualTo(context1Copy);

        // But given context from same user with different args (order matters)
        CommandRequestContext context2 = new CommandRequestContext(user, Arrays.asList("arg3", "arg2", "arg1"));

        // Then they are not equal
        assertThat(context2).isNotEqualTo(context1);

    }

}
