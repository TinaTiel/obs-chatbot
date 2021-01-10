/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.dispatch;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.RunnableAction;
import com.tinatiel.obschatbot.core.client.NoOpClient;
import com.tinatiel.obschatbot.core.request.Request;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.request.queue.ActionCommand;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.mock;

public class SequentialExecutorImplIT {

    private final ExecutorService parentExecutor = mock(ExecutorService.class);

    @RepeatedTest(5)
    void actionsAreRunByExecutorInOrderAfterEachCompletes() {

        int numRequests = 10;
        int minNumActions = 5;
        int maxNumActions = 20;
        int maxExecTimeMs = 10;
        Random random = new Random();

        // given many random requests
        List<Request> requests = new ArrayList<>();
        for(int r=0; r < numRequests; r++) {
            List<ActionCommand> actions = new ArrayList<>();
            int numActions = random.nextInt(maxNumActions) + minNumActions;
            for(int a=0; a < numActions; a++) {
                final int currRequest = r+1;
                final int currAction = a+1;
                actions.add(new StubActionCommand(
                        currRequest,
                        currAction,
                        random.nextInt(maxExecTimeMs)
                ));
            }
            requests.add(new Request(new SequentialExecutorImpl(),10L, actions));
        }

        // (print out for sanity-checking)
        for(Request request :requests) System.out.println("Generated " + request);

        // when each are executed
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for(Request request :requests) {
            executorService.submit(request);
        }
        System.out.println(Timestamp.from(Instant.now()) + " - started/scheduled executions");

        // And we wait for them all to finish executing
        executorService.shutdown();
        System.out.println(Timestamp.from(Instant.now()) + " - told executor to finish-up executions & shut-down...");
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            System.out.println(Timestamp.from(Instant.now()) + " - done");
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // Then upon inspection we find the actions were run in order
        for(Request request:requests) {
            System.out.println(request);
            for(int i = 1; i< request.getActionCommands().size(); i++) {
                StubActionCommand first = (StubActionCommand) request.getActionCommands().get(i-1);
                StubActionCommand second = (StubActionCommand) request.getActionCommands().get(i);
                long delta = second.getExecRecord().getStart() - first.getExecRecord().getStart();
                try {
                    // The actions are run in-order
                    assertThat(delta).isGreaterThanOrEqualTo(0L);

                    // And the delta is at least the expected delay
                    assertThat(delta).isGreaterThanOrEqualTo(first.getExecRecord().getExpectedDuration());

                } catch (AssertionError e) {
                    System.out.println(delta);
                    fail("Executed out of order:\nfirst : %s:%s\nsecond: %s:%s",
                            first, first.getExecRecord(),
                            second, second.getExecRecord());
                }

            }
        }

    }

    private static class ExecRecord {
        private final long expectedDuration;
        private volatile long start;

        public ExecRecord(long expectedDuration) {
            this.expectedDuration = expectedDuration;
        }

        public void setStart(long start) {
            this.start = start;
        }

        public long getExpectedDuration() {
            return expectedDuration;
        }

        public synchronized long getStart() {
            return start;
        }


        @Override
        public String toString() {
            return "ExecRecord{" +
                    "expectedDuration=" + expectedDuration +
                    ", start=" + start +
                    '}';
        }
    }

    private static class StubActionCommand extends ActionCommand {

        private final long requestNum;
        private final long actionNum;

        private final ExecRecord execRecord;

        public StubActionCommand(long requestNum, long actionNum, long execTime) {
            super(NoOpClient.class, mock(Action.class), mock(RequestContext.class));
            this.requestNum = requestNum;
            this.actionNum = actionNum;
            this.execRecord = new ExecRecord(execTime);
        }

        public ExecRecord getExecRecord() {
            return execRecord;
        }

        @Override
        public Void get() throws InterruptedException, ExecutionException {
            execRecord.setStart(System.currentTimeMillis());
            return super.get();
        }

        @Override
        public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            execRecord.setStart(System.currentTimeMillis());
            return super.get(timeout, unit);
        }

        @Override
        public String toString() {
            return String.format("Action-%d-%d", requestNum, actionNum);
        }
    }

    private static class StubAction implements Action<NoOpClient, StubAction> {

        @Override
        public Class<NoOpClient> acceptsClientType() {
            return null;
        }

        @Override
        public StubAction clone() {
            return null;
        }

        @Override
        public RunnableAction<NoOpClient, StubAction> createRunnableAction(NoOpClient client, RequestContext requestContext) {
            return null;
        }
    }

}
