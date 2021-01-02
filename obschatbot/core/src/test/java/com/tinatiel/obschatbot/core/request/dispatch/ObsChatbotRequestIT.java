/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.dispatch;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.RunnableAction;
import com.tinatiel.obschatbot.core.client.NoOpClient;
import com.tinatiel.obschatbot.core.request.ObsChatbotRequest;
import com.tinatiel.obschatbot.core.request.ObsChatbotRequestContext;
import com.tinatiel.obschatbot.core.request.dispatch.SequentialExecutor;
import org.junit.jupiter.api.RepeatedTest;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.mock;

public class ObsChatbotRequestIT {

    private final ExecutorService parentExecutor = mock(ExecutorService.class);

    @RepeatedTest(5)
    void actionsAreRunByExecutorInOrderAfterEachCompletes() {

        int numRequests = 10;
        int minNumActions = 5;
        int maxNumActions = 20;
        int maxExecTimeMs = 10;
        Random random = new Random();

        // given many random requests
        List<ObsChatbotRequest> requests = new ArrayList<>();
        for(int r=0; r < numRequests; r++) {
            List<RunnableAction> actions = new ArrayList<>();
            int numActions = random.nextInt(maxNumActions) + minNumActions;
            for(int a=0; a < numActions; a++) {
                final int currRequest = r+1;
                final int currAction = a+1;
                actions.add(new StubRunnableAction(
                        currRequest,
                        currAction,
                        random.nextInt(maxExecTimeMs)
                ));
            }
            requests.add(new ObsChatbotRequest(new SequentialExecutor(parentExecutor),Long.MAX_VALUE, actions));
        }

        // (print out for sanity-checking)
        for(ObsChatbotRequest obsChatbotRequest :requests) System.out.println("Generated " + obsChatbotRequest);

        // when each are executed
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for(ObsChatbotRequest obsChatbotRequest :requests) {
            executorService.submit(obsChatbotRequest);
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
        for(ObsChatbotRequest request:requests) {
            for(int i=1; i< request.getActions().size(); i++) {
                StubRunnableAction first = (StubRunnableAction) request.getActions().get(i-1);
                StubRunnableAction second = (StubRunnableAction) request.getActions().get(i);
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

    private static class StubRunnableAction implements RunnableAction<NoOpClient, StubAction> {

        private final long requestNum;
        private final long actionNum;

        private final ExecRecord execRecord;

        public StubRunnableAction(long requestNum, long actionNum, long execTime) {
            this.requestNum = requestNum;
            this.actionNum = actionNum;
            this.execRecord = new ExecRecord(execTime);
        }

        @Override
        public ObsChatbotRequestContext getRequestContext() {
            return null;
        }

        @Override
        public StubAction getAction() {
            return null;
        }

        @Override
        public NoOpClient getClient() {
            return null;
        }

        public ExecRecord getExecRecord() {
            return execRecord;
        }

        @Override
        public void run() {
            long currTime = System.currentTimeMillis();
            System.out.println(new Timestamp(currTime) + " - Executing " + this + " for " + execRecord.getExpectedDuration() + "ms");
            try {
                execRecord.setStart(currTime);
                Thread.sleep(execRecord.getExpectedDuration());
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
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
        public RunnableAction<NoOpClient, StubAction> createRunnableAction(NoOpClient client, ObsChatbotRequestContext obsChatbotRequestContext) {
            return null;
        }
    }

}
