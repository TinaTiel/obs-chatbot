/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.command;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.sequencer.ActionSequencer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


public class CommandTest {

    @Test
    void defaults() {

        // Given a default command with no data
        Command command = new Command();

        // Then it has the following defaults
        assertThat(command.getName()).isNull();
        assertThat(command.getActionSequencer()).isNull();
        assertThat(command.isDisabled()).isFalse();

    }

    @Test
    void buildCommand() {

        // Given data
        String name = "foo";
        ActionSequencer actionSequencer = mock(ActionSequencer.class);
        boolean disabled = true;

        // When built
        Command command = new Command()
                .name(name)
                .actionSequencer(actionSequencer)
                .disabled(disabled);

        // Then the fields return vals as expected
        assertThat(command.getName()).isEqualTo(name);
        assertThat(command.getActionSequencer()).isEqualTo(actionSequencer);
        assertThat(command.isDisabled()).isEqualTo(disabled);

    }

    @Test
    void actionsDelegatedToSequencer() {

        // Given an action sequencer with actions
        ActionSequencer actionSequencer = mock(ActionSequencer.class);
        Action action1 = mock(Action.class);
        Action action2 = mock(Action.class);
        Action action3 = mock(Action.class);
        List<Action> actions = Arrays.asList(action1, action2, action3);
        when(actionSequencer.listAll()).thenReturn(actions);

        // And a command is assigned the sequencer
        Command command = new Command().actionSequencer(actionSequencer);

        // When called for actions
        List<Action> result = command.getActions();

        // Then the (immutable) result is delegated to the sequencer
        verify(actionSequencer, times(1)).listAll();
        assertThat(result).containsExactlyElementsOf(actions);
        assertThat(result).isNotSameAs(actions); // we don't want the actions to be changed from the command

    }

    @Test
    void commandEquality() {

        // Given two instances of a command with the same name
        Command command1 = new Command().name("foo");
        Command command1copy = new Command().name("foo");

        // Then they are equal
        assertThat(command1)
                .isNotSameAs(command1copy)
                .isEqualTo(command1copy);

        // But given an instance with a different name
        Command command2 = new Command().name("bar");

        // Then they are not equal
        assertThat(command2).isNotEqualTo(command1);

        // And given two commands with null name
        Command null1 = new Command();
        Command null2 = new Command();

        // Then they are equal to each other
        assertThat(null1)
                .isNotSameAs(null2)
                .isEqualTo(null2);

        // But are not equal to others
        assertThat(command1).isNotEqualTo(null1);

    }
}
