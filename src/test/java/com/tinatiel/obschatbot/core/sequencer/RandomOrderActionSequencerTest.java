/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.sequencer;

import com.tinatiel.obschatbot.core.action.Action;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class RandomOrderActionSequencerTest {

    Action action1, action2, action3;

    @BeforeEach
    void setUp() {

        action1 = mock(Action.class);
        action2 = mock(Action.class);
        action3 = mock(Action.class);

    }

    @RepeatedTest(30)
    void executeRandomActionsIsPseudoRandomAndCyclical() {

        // Given actions to execute
        List<Action> input = Arrays.asList(action1, action2, action3);
        Map<Action, Integer> sequenceResults = new HashMap<>();
        for(Action action:input) sequenceResults.put(action, 0);

        // Given sequencer will randomly execute at most two of the actions provided
        ActionSequencer sequencer = new RandomOrderActionSequencer(2);
        sequencer.setActions(input);

        // when sequenced
        List<Action> sequence = sequencer.nextSequence();
        System.out.println("First sequence : " + sequence);

        // Then there are two picked
        assertThat(sequence).hasSize(2);

        // And two of three will have been picked once
        for(Action action:sequence) {
            sequenceResults.put(action, sequenceResults.get(action) + 1);
        }
        int matches = 0;
        for(Map.Entry entry:sequenceResults.entrySet()) {
            if(((Integer)entry.getValue()) == 1) matches ++;
        }
        assertThat(matches).isEqualTo(2);

        // And when sequenced again
        sequence = sequencer.nextSequence();
        System.out.println("Second sequence: " + sequence);

        // Then two were picked
        assertThat(sequence).hasSize(2);

        // And at least one of them will be picked twice (100% statistical certainty)
        for(Action action:sequence) {
            sequenceResults.put(action, sequenceResults.get(action) + 1);
        }
        matches = 0;
        for(Map.Entry entry:sequenceResults.entrySet()) {
            if(((Integer)entry.getValue()) == 2) matches ++;
        }
        assertThat(matches).isGreaterThanOrEqualTo(1);

    }

    @Test
    void executeRandomActionsWhenNullExecutionsThenExecuteAll() {

        // Given actions to execute
        List<Action> input = Arrays.asList(action1, action2, action3);

        // Given random order sequencer with null args
        ActionSequencer sequencer = new RandomOrderActionSequencer(null);
        sequencer.setActions(input);

        // when executed
        List<Action> sequence = sequencer.nextSequence();

        // Then all are sequenced
        assertThat(sequence).containsExactlyInAnyOrderElementsOf(input);
        assertThat(sequence).isNotSameAs(input);

        // And when executed again
        sequence = sequencer.nextSequence();

        // Then all are sequenced
        assertThat(sequence).containsExactlyInAnyOrderElementsOf(input);
        assertThat(sequence).isNotSameAs(input);

    }

    @Test
    void executeRandomActionsWhenMoreExecutionsThanActionsThenExecuteAll() {

        // Given actions to execute
        List<Action> input = Arrays.asList(action1, action2, action3);

        // Given random order sequencer with null args
        ActionSequencer sequencer = new RandomOrderActionSequencer(999);
        sequencer.setActions(input);

        // when executed
        List<Action> sequence = sequencer.nextSequence();

        // Then all are sequenced
        assertThat(sequence).containsExactlyInAnyOrderElementsOf(input);
        assertThat(sequence).isNotSameAs(input);

        // And when executed again
        sequence = sequencer.nextSequence();

        // Then all are sequenced
        assertThat(sequence).containsExactlyInAnyOrderElementsOf(input);
        assertThat(sequence).isNotSameAs(input);

    }

    @RepeatedTest(10)
    void randomSequenceRepeatedInvocationOfGetActionsReturnsSameResult() {

        // Given actions to execute
        List<Action> input = Arrays.asList(action1, action2, action3);

        // Given random order sequencer, where pick per execution is irrelevant
        ActionSequencer sequencer = new RandomOrderActionSequencer(1);
        sequencer.setActions(input);

        // when executed, the result of getActions is the same after each execution
        sequencer.nextSequence();
        assertThat(sequencer.listAll()).containsExactlyElementsOf(input);

        sequencer.nextSequence();
        assertThat(sequencer.listAll()).containsExactlyElementsOf(input);

        sequencer.nextSequence();
        assertThat(sequencer.listAll()).containsExactlyElementsOf(input);

        sequencer.nextSequence();
        assertThat(sequencer.listAll()).containsExactlyElementsOf(input);

        sequencer.nextSequence();
        assertThat(sequencer.listAll()).containsExactlyElementsOf(input);

    }

    @Test
    void nullPickWithUnsetActions() {

        // Given null pick
        ActionSequencer sequencer = new RandomOrderActionSequencer(null);

        // When sequenced without any actions
        List<Action> result = sequencer.nextSequence();

        // Then an empty list is returned
        assertThat(result).isEmpty();

    }
}
