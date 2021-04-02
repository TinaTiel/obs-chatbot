package com.tinatiel.obschatbot.core.request.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WorkGroupManagerTest {

    WorkGroupRouter router;
    WorkGroupManager workGroupManager;

    @BeforeEach
    void setUp() {
        router = mock(WorkGroupRouter.class);
        workGroupManager = new WorkGroupManagerImpl(router);
    }

    @Test
    void workGroupWithHigherPriorityReturnedFirst() {

        // Given a router returning two workgroups
        WorkGroup wg1 = mock(WorkGroup.class);
        WorkGroup wg2 = mock(WorkGroup.class);
        WorkGroup wg3 = mock(WorkGroup.class);
        when(router.workGroupsByPriority()).thenReturn(Arrays.asList(wg1, wg2, wg3));

        // And given each has the same work
        when(wg1.getNumberOfWorkableRequests()).thenReturn(3);
        when(wg2.getNumberOfWorkableRequests()).thenReturn(3);
        when(wg3.getNumberOfWorkableRequests()).thenReturn(3);

        // When the next Wg is requested
        WorkGroup result = workGroupManager.getNext();

        // Then the higher priority one is returned
        assertThat(result).isEqualTo(wg1);

    }

    @Test
    void workGroupWithWorkReturnedIfHigherPriorityHasNone() {

        // Given a router returning two workgroups
        WorkGroup wg1 = mock(WorkGroup.class);
        WorkGroup wg2 = mock(WorkGroup.class);
        WorkGroup wg3 = mock(WorkGroup.class);
        when(router.workGroupsByPriority()).thenReturn(Arrays.asList(wg1, wg2, wg3));

        // And given the lower priority has work, but the highest does not
        when(wg1.getNumberOfWorkableRequests()).thenReturn(0);
        when(wg2.getNumberOfWorkableRequests()).thenReturn(3);
        when(wg3.getNumberOfWorkableRequests()).thenReturn(3);

        // When the next Wg is requested
        WorkGroup result = workGroupManager.getNext();

        // Then the next highest priority with work is returned
        assertThat(result).isEqualTo(wg2);


    }

    @Test
    void returnSpecialWorkgroupWhenNoWorkgroupsHaveAnyWork() {

        // Given a router returning two workgroups
        WorkGroup wg1 = mock(WorkGroup.class);
        WorkGroup wg2 = mock(WorkGroup.class);
        WorkGroup wg3 = mock(WorkGroup.class);
        when(router.workGroupsByPriority()).thenReturn(Arrays.asList(wg1, wg2, wg3));

        // And given none have work
        when(wg1.getNumberOfWorkableRequests()).thenReturn(0);
        when(wg2.getNumberOfWorkableRequests()).thenReturn(0);
        when(wg3.getNumberOfWorkableRequests()).thenReturn(0);

        // When the next Wg is requested
        WorkGroup result = workGroupManager.getNext();

        // Then null is returned
        assertThat(result).isInstanceOf(NoWorkAvailableWorkGroup.class);

    }
}
