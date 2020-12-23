package com.tinatiel.obschatbot.core.action.sequencer;

import com.tinatiel.obschatbot.core.action.Action;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ActionSequencerTest {

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
        ActionSequencer sequencer = new InOrderActionSequencer(input, false);

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
        ActionSequencer sequencer = new InOrderActionSequencer(input, true);

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

    @RepeatedTest(30)
    void executeRandomActionsIsPseudoRandomAndCyclical() {

        // Given actions to execute
        List<Action> input = Arrays.asList(action1, action2, action3);
        Map<Action, Integer> sequenceResults = new HashMap<>();
        for(Action action:input) sequenceResults.put(action, 0);

        // Given sequencer will randomly execute at most two of the actions provided
        ActionSequencer sequencer = new RandomOrderActionSequencer(input, 2);

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
        ActionSequencer sequencer = new RandomOrderActionSequencer(input, null);

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
        ActionSequencer sequencer = new RandomOrderActionSequencer(input, 999);

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
        ActionSequencer sequencer = new RandomOrderActionSequencer(input, 1);

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
}
