package com.tinatiel.obschatbot.core.action;

import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.dispatch.CommandRequest;
import com.tinatiel.obschatbot.core.user.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class CommandRequestTest {

    @Test
    void contextUniqueByUser() {

        // Given an user and command
        User user1 = mock(User.class);
        Command command = mock(Command.class);

        // Given contexts from the same user, everything else the same
        CommandRequest context1 = new CommandRequest(user1, command, new ArrayList<>());
        CommandRequest context1Copy = new CommandRequest(user1, command, new ArrayList<>());

        // Then they are equal
        assertThat(context1)
                .isNotSameAs(context1Copy)
                .isEqualTo(context1Copy);

        // But given context from different user
        User user2 = mock(User.class);
        CommandRequest context2 = new CommandRequest(user2, command, new ArrayList<>());

        // Then they are not equal
        assertThat(context2).isNotEqualTo(context1);

    }

    @Test
    void contextUniqueByArgs() {

        // Given an user and command
        User user = mock(User.class);
        Command command = mock(Command.class);

        // Given contexts from the same user, everything else the same
        CommandRequest context1 = new CommandRequest(user, command, Arrays.asList("arg1", "arg2", "arg3"));
        CommandRequest context1Copy = new CommandRequest(user, command, Arrays.asList("arg1", "arg2", "arg3"));

        // Then they are equal
        assertThat(context1)
                .isNotSameAs(context1Copy)
                .isEqualTo(context1Copy);

        // But given context from same user with different args (order matters)
        CommandRequest context2 = new CommandRequest(user, command, Arrays.asList("arg3", "arg2", "arg1"));

        // Then they are not equal
        assertThat(context2).isNotEqualTo(context1);

    }

    @Test
    void contextUniqueByCommand() {

        // Given an user and command
        User user = mock(User.class);
        Command command1 = mock(Command.class);

        // Given contexts from the same user, everything else the same
        CommandRequest context1 = new CommandRequest(user, command1, new ArrayList<>());
        CommandRequest context1Copy = new CommandRequest(user, command1, new ArrayList<>());

        // Then they are equal
        assertThat(context1)
                .isNotSameAs(context1Copy)
                .isEqualTo(context1Copy);

        // But given context from same user for a different command
        Command command2 = mock(Command.class);
        CommandRequest context2 = new CommandRequest(user, command2, new ArrayList<>());

        // Then they are not equal
        assertThat(context2).isNotEqualTo(context1);

    }

}
