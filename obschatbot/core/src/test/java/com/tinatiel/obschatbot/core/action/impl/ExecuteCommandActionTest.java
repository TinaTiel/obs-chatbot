package com.tinatiel.obschatbot.core.action.impl;

import com.tinatiel.obschatbot.core.command.Command;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ExecuteCommandActionTest {

    @Test
    void buildAction() {

        // Given a command
        Command command = mock(Command.class);

        // When action is built
        ExecuteCommandAction action = new ExecuteCommandAction(command);

        // Then it has the command specified
        assertThat(action.getTarget()).isEqualTo(command);

    }

    @Test
    void commandRequired() {

        assertThatThrownBy(() -> {
            ExecuteCommandAction action = new ExecuteCommandAction(null);
        }).isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void runDoesNothing() {

        // Given a command
        Command command = mock(Command.class);

        // And an action is built
        ExecuteCommandAction action = new ExecuteCommandAction(command);

        // When run
        action.run();

        // Then it runs; there is nothing else to check

    }

}
