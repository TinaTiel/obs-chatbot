package com.tinatiel.obschatbot.core.action;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class ActionContextTest {

    @Test
    void contextUniqueByUser() {

        // Given contexts from the same user, everything else the same
        ActionContext context1 = new ActionContext("user1", "foo", new ArrayList<>());
        ActionContext context1Copy = new ActionContext("user1", "foo", new ArrayList<>());

        // Then they are equal
        assertThat(context1)
                .isNotSameAs(context1Copy)
                .isEqualTo(context1Copy);

        // But given context from different user
        ActionContext context2 = new ActionContext("user2", "foo", new ArrayList<>());

        // Then they are not equal
        assertThat(context2).isNotEqualTo(context1);

    }

    @Test
    void contextUniqueByArgs() {

        // Given contexts from the same user, everything else the same
        ActionContext context1 = new ActionContext("user1", "foo", Arrays.asList("arg1", "arg2", "arg3"));
        ActionContext context1Copy = new ActionContext("user1", "foo", Arrays.asList("arg1", "arg2", "arg3"));

        // Then they are equal
        assertThat(context1)
                .isNotSameAs(context1Copy)
                .isEqualTo(context1Copy);

        // But given context from same user with different args (order matters)
        ActionContext context2 = new ActionContext("user1", "foo", Arrays.asList("arg3", "arg2", "arg1"));

        // Then they are not equal
        assertThat(context2).isNotEqualTo(context1);

    }

    @Test
    void contextUniqueByCommand() {

        // Given contexts from the same user, everything else the same
        ActionContext context1 = new ActionContext("user1", "foo", new ArrayList<>());
        ActionContext context1Copy = new ActionContext("user1", "foo", new ArrayList<>());

        // Then they are equal
        assertThat(context1)
                .isNotSameAs(context1Copy)
                .isEqualTo(context1Copy);

        // But given context from same user for a different command
        ActionContext context2 = new ActionContext("user1", "bar", new ArrayList<>());

        // Then they are not equal
        assertThat(context2).isNotEqualTo(context1);

    }

}
