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

        // In the first request, we get back the first item from
        // each command as expected
        assertThat(workGroup.getNextWorkBatch()).containsExactly(
                request1.getActionCommands().get(0),
                request2.getActionCommands().get(0),
                request3.getActionCommands().get(0)
        );
        // And there are still 3 commands with work to do
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(3);

        // In the second request, we take the last action from CommandRequest #2
        assertThat(workGroup.getNextWorkBatch()).containsExactly(
                request1.getActionCommands().get(1),
                request2.getActionCommands().get(1), // last request
                request3.getActionCommands().get(1)
        );
        // ...so now we have only 2 commands with work left
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(2);

        // In the third request, we take the last action from CommandRequest #1
        assertThat(workGroup.getNextWorkBatch()).containsExactly(
                request1.getActionCommands().get(2), // last request
                request3.getActionCommands().get(2)
        );
        // ...so now we have only 1 command with work left
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(1);

        // Finally in the last (fourth) request, we take the last action from CommandRequest #3
        assertThat(workGroup.getNextWorkBatch()).containsExactly(
                request3.getActionCommands().get(3) // last request
        );
        // ...and now we have no commands left with work to do
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

        // calling again, 1 is blocked so it is not returned
        assertThat(workGroup.getNextWorkBatch()).containsExactly(
                // 1 is blocked
                request2.getActionCommands().get(1), // will block
                request3.getActionCommands().get(1)
        );
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(3);

        // Unblocking #1
        workGroup.free(request1.getActionCommands().get(0));

        // Now #1 is returned since it is unblocked, but now #2 is blocked so we don't see it
        assertThat(workGroup.getNextWorkBatch()).containsExactly(
                request1.getActionCommands().get(2), // last item
                // 2 is blocked
                request3.getActionCommands().get(2)
        );
        // Note that since #2 is blocked its command is still in-flight.
        // But at the same time, we have finished #1 so it is now empty
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(2);

        // Unblocking #2
        workGroup.free(request2.getActionCommands().get(1));

        // Now that #2 is unblocked, we'll see it
        assertThat(workGroup.getNextWorkBatch()).containsExactly(
                // 1 is empty
                request2.getActionCommands().get(1), // last item
                request3.getActionCommands().get(3) // will block. ALSO the last item
        );
        // And since #2 was emptied out, we now have one less in-flight commandRequest
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(1);

        // #3 was blocked, so this request turns up nothing
        assertThat(workGroup.getNextWorkBatch()).containsExactly(
                // 1 is empty
                // 2 is empty
                // 3 is blocked
        );
        // #3 is still in-flight
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(1);

        // Calling again yields the same results as before; we haven't unblocked #3 yet
        assertThat(workGroup.getNextWorkBatch()).containsExactly(
                // 1 is empty
                // 2 is empty
                // 3 is blocked
        );
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(1);

        // free #3
        workGroup.free(request3.getActionCommands().get(3));

        // Calling again yields similar results because #3's last request happened
        // to be blocking
        assertThat(workGroup.getNextWorkBatch()).containsExactly(
                // 1 is empty
                // 2 is empty
                // 3 is empty
        );
        // But what we have done is now freed up #3 and no longer have it inflight
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
