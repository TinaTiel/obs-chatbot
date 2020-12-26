package com.tinatiel.obschatbot.core.action;

import com.tinatiel.obschatbot.core.dispatch.CommandRequest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandRequestTest {

//    @Test
//    void contextUniqueByUser() {
//
//        // Given contexts from the same user, everything else the same
//        CommandRequest context1 = new CommandRequest("user1", "foo", new ArrayList<>());
//        CommandRequest context1Copy = new CommandRequest("user1", "foo", new ArrayList<>());
//
//        // Then they are equal
//        assertThat(context1)
//                .isNotSameAs(context1Copy)
//                .isEqualTo(context1Copy);
//
//        // But given context from different user
//        CommandRequest context2 = new CommandRequest("user2", "foo", new ArrayList<>());
//
//        // Then they are not equal
//        assertThat(context2).isNotEqualTo(context1);
//
//    }
//
//    @Test
//    void contextUniqueByArgs() {
//
//        // Given contexts from the same user, everything else the same
//        CommandRequest context1 = new CommandRequest("user1", "foo", Arrays.asList("arg1", "arg2", "arg3"));
//        CommandRequest context1Copy = new CommandRequest("user1", "foo", Arrays.asList("arg1", "arg2", "arg3"));
//
//        // Then they are equal
//        assertThat(context1)
//                .isNotSameAs(context1Copy)
//                .isEqualTo(context1Copy);
//
//        // But given context from same user with different args (order matters)
//        CommandRequest context2 = new CommandRequest("user1", "foo", Arrays.asList("arg3", "arg2", "arg1"));
//
//        // Then they are not equal
//        assertThat(context2).isNotEqualTo(context1);
//
//    }
//
//    @Test
//    void contextUniqueByCommand() {
//
//        // Given contexts from the same user, everything else the same
//        CommandRequest context1 = new CommandRequest("user1", "foo", new ArrayList<>());
//        CommandRequest context1Copy = new CommandRequest("user1", "foo", new ArrayList<>());
//
//        // Then they are equal
//        assertThat(context1)
//                .isNotSameAs(context1Copy)
//                .isEqualTo(context1Copy);
//
//        // But given context from same user for a different command
//        CommandRequest context2 = new CommandRequest("user1", "bar", new ArrayList<>());
//
//        // Then they are not equal
//        assertThat(context2).isNotEqualTo(context1);
//
//    }

}
