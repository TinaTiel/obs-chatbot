/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.sequencer;

import com.tinatiel.obschatbot.core.action.Action;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class InOrderActionSequencerTest {

    Action action1, action2, action3;

    @BeforeEach
    void setUp() {

        action1 = mock(Action.class);
        action2 = mock(Action.class);
        action3 = mock(Action.class);

    }

    @Test
    void sequenceActionsInOrder() {

        // Given actions to execute
        List<Action> input = Arrays.asList(action1, action2, action3);

        // Given an in-order sequencer
        ActionSequencer sequencer = new InOrderActionSequencer(false);
        sequencer.setActions(input);

        // When sequenced
        List<Action> sequence = sequencer.nextSequence();

        // Then the actions are scheduled in the same order they went in
        assertThat(sequence).containsExactly(action1, action2, action3);
        assertThat(sequence).isNotSameAs(input);

        // And when sequenced again
        sequence = sequencer.nextSequence();

        // Then the actions are still sequenced in the same order
        assertThat(sequence).containsExactly(action1, action2, action3);
        assertThat(sequence).isNotSameAs(input);

        // And getActions returns the original input
        assertThat(sequencer.listAll()).containsExactlyElementsOf(input);

    }

    @Test
    void executeActionsInReverseOrder() {

        // Given actions to execute
        List<Action> input = Arrays.asList(action1, action2, action3);

        // Given an in-order scheduler, but reversed
        ActionSequencer sequencer = new InOrderActionSequencer(true);
        sequencer.setActions(input);

        // When scheduled
        List<Action> sequence = sequencer.nextSequence();

        // Then the actions are scheduled in the opposite order they went in
        assertThat(sequence).containsExactly(action3, action2, action1);
        assertThat(sequence).isNotSameAs(input);

        // And when sequenced again
        sequence = sequencer.nextSequence();

        // Then the actions are still sequenced in the same order
        assertThat(sequence).containsExactly(action3, action2, action1);
        assertThat(sequence).isNotSameAs(input);

        // And getActions returns the original input
        assertThat(sequencer.listAll()).containsExactlyElementsOf(input);

    }

}
