package com.tinatiel.obschatbot.core.request.scheduler;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NoWorkAvailableWorkGroupTest {

    WorkGroup workGroup = new NoWorkAvailableWorkGroup();

    @Test
    void alwaysReturnEmptyList() {

        // Repeatedly calling it doesn't change result
        for(int i=0; i< 100; i++) {
            assertThat(workGroup.getNextWorkBatch()).isNotNull().isEmpty();
        }

    }

    @Test
    void noWorkToDoOrInFlight() {

        assertThat(workGroup.getNumberOfWorkableRequests()).isZero();
        assertThat(workGroup.getNumberOfInflightRequests()).isZero();

    }

}
