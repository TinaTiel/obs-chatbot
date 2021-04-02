package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.CommandRequest;
import com.tinatiel.obschatbot.core.request.RequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
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
                new NonblockingAction("1.1"),
                new NonblockingAction("1.2"),
                new NonblockingAction("1.3")
        ));
        CommandRequest request2 = new CommandRequest(mockContext, Arrays.asList(
                new NonblockingAction("2.1"),
                new NonblockingAction("2.2")
        ));
        CommandRequest request3 = new CommandRequest(mockContext, Arrays.asList(
                new NonblockingAction("3.1"),
                new NonblockingAction("3.2"),
                new NonblockingAction("3.3"),
                new NonblockingAction("3.4")
        ));

        // And are added to the workgroup
        workGroup.add(request1);
        workGroup.add(request2);
        workGroup.add(request3);

        // Then the number of inflight requests is the same as
        // the number of requests we just added
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(3);
        assertThat(workGroup.getNumberOfWorkableRequests()).isEqualTo(3);

        // When we generate the next batches, then each batch
        // is served round-robin from each request

        // Initial state:
        // blocked:
        //  (none)
        // available:
        //  1: 1.1, 1.2, 1.3
        //  2: 2.1, 2.2
        //  3: 3.1, 3.2, 3.3, 3.4

        // In the first request, we get back the first item from
        // each command as expected
        assertThat(workGroup.getNextWorkBatch()).containsExactlyInAnyOrder(
                // 1.1, 2.1, 3.1
                request1.getActionCommands().get(0),
                request2.getActionCommands().get(0),
                request3.getActionCommands().get(0)
        );
        // blocked:
        //  (none)
        // available:
        //  1: 1.2, 1.3
        //  2: 2.2
        //  3: 3.2, 3.3, 3.4
        // And there are still 3 commands with work to do
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(3);
        assertThat(workGroup.getNumberOfWorkableRequests()).isEqualTo(3);

        // In the second request, we take the last action from CommandRequest #2
        assertThat(workGroup.getNextWorkBatch()).containsExactlyInAnyOrder(
                // 1.2, 2.2, 3.3
                request1.getActionCommands().get(1),
                request2.getActionCommands().get(1),
                request3.getActionCommands().get(1)
        );
        // blocked:
        //  (none)
        // available:
        //  1: 1.3
        //  3: 3.3, 3.4
        // ...so now we have only 2 commands with work left
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(2);
        assertThat(workGroup.getNumberOfWorkableRequests()).isEqualTo(2);

        // In the third request, we take the last action from CommandRequest #1
        assertThat(workGroup.getNextWorkBatch()).containsExactlyInAnyOrder(
                // 1.3, 3.3
                request1.getActionCommands().get(2),
                request3.getActionCommands().get(2)
        );
        // blocked:
        //  (none)
        // available:
        //  3: 3.4
        // ...so now we have only 1 command with work left
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(1);
        assertThat(workGroup.getNumberOfWorkableRequests()).isEqualTo(1);

        // Finally in the last (fourth) request, we take the last action from CommandRequest #3
        assertThat(workGroup.getNextWorkBatch()).containsExactlyInAnyOrder(
                // 3.4
                request3.getActionCommands().get(3) // last request
        );
        // blocked:
        //  (none)
        // available:
        //  (none)
        // ...and now we have no commands left with work to do
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(0);
        assertThat(workGroup.getNumberOfWorkableRequests()).isEqualTo(0);

    }

    @Test
    void blockingRequestsAreSkippedInBatchesUntilFreed() {

        // Given a mock context (doesn't matter at the WorkGroup level)
        RequestContext mockContext = mock(RequestContext.class);

        // Given three commands to execute, some with blocking commands
        CommandRequest request1 = new CommandRequest(mockContext, Arrays.asList(
                new BlockingAction("1.1b"),
                new NonblockingAction("1.2"),
                new NonblockingAction("1.3")
        ));
        CommandRequest request2 = new CommandRequest(mockContext, Arrays.asList(
                new NonblockingAction("2.1"),
                new BlockingAction("2.2b")
        ));
        CommandRequest request3 = new CommandRequest(mockContext, Arrays.asList(
                new NonblockingAction("3.1"),
                new NonblockingAction("3.2"),
                new BlockingAction("3.3b"),
                new NonblockingAction("3.4")
        ));

        // And are added to the workgroup
        workGroup.add(request1);
        workGroup.add(request2);
        workGroup.add(request3);

        // When we generate the next batches, then each batch
        // is served round-robin from each request, ignoring blocked ones until unblocked

        // Initial state:
        // blocked:
        //  (none)
        // available:
        //  1: 1.1b, 1.2, 1.3
        //  2: 2.1, 2.2b
        //  3: 3.1, 3.2, 3.3b, 3.4

        // next batch
        assertThat(workGroup.getNextWorkBatch()).containsExactlyInAnyOrder(
                // 1.1b, 2.1, 3.1
                request1.getActionCommands().get(0),
                request2.getActionCommands().get(0),
                request3.getActionCommands().get(0)
        );
        // blocked:
        //  1: 1.2, 1.3
        // available:
        //  2: 2.2b
        //  3: 3.2, 3.3b, 3.4
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(3);
        assertThat(workGroup.getNumberOfWorkableRequests()).isEqualTo(2);

        // calling again
        assertThat(workGroup.getNextWorkBatch()).containsExactlyInAnyOrder(
                // 2.2b, 3.2
                request2.getActionCommands().get(1),
                request3.getActionCommands().get(1)
        );
        // blocked:
        //  1: 1.2, 1.3
        //  2: (empty)
        // available:
        //  3: 3.3b, 3.4
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(3);
        assertThat(workGroup.getNumberOfWorkableRequests()).isEqualTo(1);

        // Unblocking #1
        workGroup.free(request1.getActionCommands().get(0));
        // blocked:
        //  2: (empty)
        // available:
        //  1: 1.2, 1.3
        //  3: 3.3b, 3.4
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(3);
        assertThat(workGroup.getNumberOfWorkableRequests()).isEqualTo(2);

        // Now #1 is returned since it is unblocked, but now #2 is blocked so we don't see it
        assertThat(workGroup.getNextWorkBatch()).containsExactlyInAnyOrder(
                // 1.2, 3.3b
                request1.getActionCommands().get(1),
                request3.getActionCommands().get(2)
        );
        // blocked:
        //  2: (empty)
        //  3: 3.4
        // available:
        //  1: 1.3
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(3);
        assertThat(workGroup.getNumberOfWorkableRequests()).isEqualTo(1);

        // Unblocking #2
        workGroup.free(request2.getActionCommands().get(1));
        // blocked:
        //  3: 3.4
        // available:
        //  1: 1.3

        // Now that #2 is unblocked, we'll see it
        assertThat(workGroup.getNextWorkBatch()).containsExactlyInAnyOrder(
                // 1.3
                request1.getActionCommands().get(2)
        );
        // blocked:
        //  3: 3.4
        // available:
        //  (none)
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(1);
        assertThat(workGroup.getNumberOfWorkableRequests()).isEqualTo(0);

        // #3 was blocked, so this request turns up nothing
        assertThat(workGroup.getNextWorkBatch()).isEmpty();
        // blocked:
        //  3: 3.4
        // available:
        //  (none)
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(1);
        assertThat(workGroup.getNumberOfWorkableRequests()).isEqualTo(0);

        // Calling again yields the same results as before; we haven't unblocked #3 yet
        assertThat(workGroup.getNextWorkBatch()).isEmpty();
        // blocked:
        //  3: 3.4
        // available:
        //  (none)
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(1);
        assertThat(workGroup.getNumberOfWorkableRequests()).isEqualTo(0);

        // free #3
        workGroup.free(request3.getActionCommands().get(2));
        // blocked:
        //  (none)
        // available:
        //  3: 3.4
        assertThat(workGroup.getNumberOfWorkableRequests()).isEqualTo(1);

        // Calling again yields similar results because #3's last request happened
        // to be blocking
        assertThat(workGroup.getNextWorkBatch()).containsExactlyInAnyOrder(
                // 3.4
                request3.getActionCommands().get(3)
        );
        // blocked:
        //  (none)
        // available:
        //  (none)
        assertThat(workGroup.getNumberOfInflightRequests()).isEqualTo(0);
        assertThat(workGroup.getNumberOfWorkableRequests()).isEqualTo(0);

    }

    @Test
    void freeingAnActionThatIsntFoundDoesNothing() {

        // Given a mock context (doesn't matter at the WorkGroup level)
        RequestContext mockContext = mock(RequestContext.class);

        // Given a command to execute
        CommandRequest request = new CommandRequest(mockContext, Arrays.asList(
                new NonblockingAction("1.1"),
                new NonblockingAction("1.2"),
                new NonblockingAction("1.3")
        ));

        // And command is added to the workgroup
        workGroup.add(request);

        // When we try to free something that isn't blocked, then nothing happens
        try {
            workGroup.free(request.getActionCommands().get(1)); // we haven't pulled anything yet, nothing is blocking
        } catch (Exception e) {
            fail("expected no exception to be thrown", e);
        }

    }

    private static class NonblockingAction implements Action<NonblockingAction> {

        private final String name;

        public NonblockingAction(String name) {
            this.name = name;
        }

        @Override
        public NonblockingAction clone() {
            return null; // ignore
        }

        @Override
        public boolean requiresCompletion() {
            return false;
        }

        @Override
        public String toString() {
            return "NonblockingAction{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    private static class BlockingAction implements Action<BlockingAction> {

        private final String name;

        public BlockingAction(String name) {
            this.name = name;
        }

        @Override
        public BlockingAction clone() {
            return null; // ignore
        }

        @Override
        public boolean requiresCompletion() {
            return true;
        }

        @Override
        public String toString() {
            return "BlockingAction{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

}
