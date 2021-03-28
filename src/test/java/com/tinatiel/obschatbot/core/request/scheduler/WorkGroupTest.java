package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.CommandRequest;
import com.tinatiel.obschatbot.core.request.RequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class WorkGroupTest {

    WorkGroup workGroup;

    @BeforeEach
    void setUp() {
        workGroup = new WorkGroupImpl();
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(0);
    }

    @Test
    void addingNonblockingRequestsGeneratesExpectedBatches() {

        // Given a mock context (doesn't matter at the WorkGroup level)
        RequestContext mockContext = mock(RequestContext.class);

        // Given three commands to execute, with an uneven number each
        CommandRequest request1 = new CommandRequest(mockContext, Arrays.asList(
                new ActionRequest(mockContext, new NonblockingAction()),
                new ActionRequest(mockContext, new NonblockingAction()),
                new ActionRequest(mockContext, new NonblockingAction())
        ));
        CommandRequest request2 = new CommandRequest(mockContext, Arrays.asList(
                new ActionRequest(mockContext, new NonblockingAction()),
                new ActionRequest(mockContext, new NonblockingAction())
        ));
        CommandRequest request3 = new CommandRequest(mockContext, Arrays.asList(
                new ActionRequest(mockContext, new NonblockingAction()),
                new ActionRequest(mockContext, new NonblockingAction()),
                new ActionRequest(mockContext, new NonblockingAction()),
                new ActionRequest(mockContext, new NonblockingAction())
        ));

        // And are added to the workgroup
        workGroup.add(request1);
        workGroup.add(request2);
        workGroup.add(request3);

        // Then the number of inflight requests is the same as
        // the number of requests we just added
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(3);

        // When we generate the next batches, then each batch
        // is served round-robin from each request
        assertThat(workGroup.getNextWorkBatch()).containsExactly(
                request1.getActionCommands().get(0),
                request2.getActionCommands().get(0),
                request3.getActionCommands().get(0)
        );
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(3);

        assertThat(workGroup.getNextWorkBatch()).containsExactly(
                request1.getActionCommands().get(1),
                request2.getActionCommands().get(1),
                request3.getActionCommands().get(1)
        );
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(2);

        assertThat(workGroup.getNextWorkBatch()).containsExactly(
                request1.getActionCommands().get(2),
                request3.getActionCommands().get(2)
        );
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(1);

        assertThat(workGroup.getNextWorkBatch()).containsExactly(
                request3.getActionCommands().get(3)
        );
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(0);

    }

    @Test
    void blockingRequestsAreSkippedInBatchesUntilFreed() {

        // Given a mock context (doesn't matter at the WorkGroup level)
        RequestContext mockContext = mock(RequestContext.class);

        // Given three commands to execute, some with blocking commands
        CommandRequest request1 = new CommandRequest(mockContext, Arrays.asList(
                new ActionRequest(mockContext, new BlockingAction()),   // block
                new ActionRequest(mockContext, new NonblockingAction()),
                new ActionRequest(mockContext, new NonblockingAction())
        ));
        CommandRequest request2 = new CommandRequest(mockContext, Arrays.asList(
                new ActionRequest(mockContext, new NonblockingAction()),
                new ActionRequest(mockContext, new BlockingAction())    // block
        ));
        CommandRequest request3 = new CommandRequest(mockContext, Arrays.asList(
                new ActionRequest(mockContext, new NonblockingAction()),
                new ActionRequest(mockContext, new NonblockingAction()),
                new ActionRequest(mockContext, new NonblockingAction()),
                new ActionRequest(mockContext, new BlockingAction())    // block
        ));

        // And are added to the workgroup
        workGroup.add(request1);
        workGroup.add(request2);
        workGroup.add(request3);

        // When we generate the next batches, then each batch
        // is served round-robin from each request, ignoring blocked ones until unblocked
        assertThat(workGroup.getNextWorkBatch()).containsExactly(
                request1.getActionCommands().get(0), // will block
                request2.getActionCommands().get(0),
                request3.getActionCommands().get(0)
        );
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(3);

        assertThat(workGroup.getNextWorkBatch()).containsExactly(
                // 1 is blocked
                request2.getActionCommands().get(1), // will block
                request3.getActionCommands().get(1)
        );
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(3);

        // Unblocking #1
        workGroup.free(request1.getActionCommands().get(0));

        assertThat(workGroup.getNextWorkBatch()).containsExactly(
                request1.getActionCommands().get(2),
                // 2 is blocked
                request3.getActionCommands().get(2)
        );
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(3);

        // Unblocking #2
        workGroup.free(request2.getActionCommands().get(1));

        assertThat(workGroup.getNextWorkBatch()).containsExactly(
                // 1 is empty
                request2.getActionCommands().get(1),
                request3.getActionCommands().get(3) // will block
        );
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(2);


        assertThat(workGroup.getNextWorkBatch()).containsExactly(
                // 1 is empty
                // 2 is empty
                // 3 is blocked
        );
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(1);

        assertThat(workGroup.getNextWorkBatch()).containsExactly(
                // 1 is empty
                // 2 is empty
                // 3 is blocked
        );
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(1);

        // free #3
        workGroup.free(request3.getActionCommands().get(3));

        assertThat(workGroup.getNextWorkBatch()).containsExactly(
                // 1 is empty
                // 2 is empty
                // 3 is empty
        );
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(0);

    }

    private static class NonblockingAction implements Action<NonblockingAction> {

        @Override
        public NonblockingAction clone() {
            return null; // ignore
        }

        @Override
        public boolean requiresCompletion() {
            return false;
        }
    }

    private static class BlockingAction implements Action<BlockingAction> {

        @Override
        public BlockingAction clone() {
            return null; // ignore
        }

        @Override
        public boolean requiresCompletion() {
            return true;
        }
    }

}
