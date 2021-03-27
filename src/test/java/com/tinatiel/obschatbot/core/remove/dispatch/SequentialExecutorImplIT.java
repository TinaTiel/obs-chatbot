/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.remove.dispatch;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.client.NoOpClient;
import com.tinatiel.obschatbot.core.request.CommandRequest;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.remove.queue.MainQueue;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;

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

@Disabled // TODO: Re-use parts of this test to verify in-order execution on Request execution
public class SequentialExecutorImplIT {

//    private final ExecutorService parentExecutor = mock(ExecutorService.class);
//
//    @RepeatedTest(5)
//    void actionsAreRunByExecutorInOrderAfterEachCompletes() {
//
//        int numRequests = 10;
//        int minNumActions = 5;
//        int maxNumActions = 20;
//        int maxExecTimeMs = 10;
//        Random random = new Random();
//        MainQueue mainQueue = new MainQueue();
//
//        // given many random requests
//        List<CommandRequest> commandRequests = new ArrayList<>();
//        for(int r=0; r < numRequests; r++) {
//            List<ActionRequest> actions = new ArrayList<>();
//            int numActions = random.nextInt(maxNumActions) + minNumActions;
//            for(int a=0; a < numActions; a++) {
//                final int currRequest = r+1;
//                final int currAction = a+1;
//                actions.add(new StubActionRequest(
//                        currRequest,
//                        currAction,
//                        random.nextInt(maxExecTimeMs)
//                ));
//            }
////            commandRequests.add(new CommandRequest(mainQueue,10L, actions));
//        }
//
//        // (print out for sanity-checking)
//        for(CommandRequest commandRequest : commandRequests) System.out.println("Generated " + commandRequest);
//
//        // when each are executed
//        ExecutorService executorService = Executors.newFixedThreadPool(10);
//        for(CommandRequest commandRequest : commandRequests) {
////            executorService.submit(commandRequest);
//        }
//        System.out.println(Timestamp.from(Instant.now()) + " - started/scheduled executions");
//
//        // And we wait for them all to finish executing
//        executorService.shutdown();
//        System.out.println(Timestamp.from(Instant.now()) + " - told executor to finish-up executions & shut-down...");
//        try {
//            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
//            System.out.println(Timestamp.from(Instant.now()) + " - done");
//        } catch (InterruptedException interruptedException) {
//            interruptedException.printStackTrace();
//        }
//
//        // Then upon inspection we find the actions were run in order
//        for(CommandRequest commandRequest : commandRequests) {
//            System.out.println(commandRequest);
//            for(int i = 1; i< commandRequest.getActionCommands().size(); i++) {
//                StubActionRequest first = (StubActionRequest) commandRequest.getActionCommands().get(i-1);
//                StubActionRequest second = (StubActionRequest) commandRequest.getActionCommands().get(i);
//                long delta = second.getExecRecord().getStart() - first.getExecRecord().getStart();
//                try {
//                    // The actions are run in-order
//                    assertThat(delta).isGreaterThanOrEqualTo(0L);
//
//                    // And the delta is at least the expected delay
//                    assertThat(delta).isGreaterThanOrEqualTo(first.getExecRecord().getExpectedDuration());
//
//                } catch (AssertionError e) {
//                    System.out.println(delta);
//                    fail("Executed out of order:\nfirst : %s:%s\nsecond: %s:%s",
//                            first, first.getExecRecord(),
//                            second, second.getExecRecord());
//                }
//
//            }
//        }
//
//    }
//
//    private static class ExecRecord {
//        private final long expectedDuration;
//        private volatile long start;
//
//        public ExecRecord(long expectedDuration) {
//            this.expectedDuration = expectedDuration;
//        }
//
//        public void setStart(long start) {
//            this.start = start;
//        }
//
//        public long getExpectedDuration() {
//            return expectedDuration;
//        }
//
//        public synchronized long getStart() {
//            return start;
//        }
//
//
//        @Override
//        public String toString() {
//            return "ExecRecord{" +
//                    "expectedDuration=" + expectedDuration +
//                    ", start=" + start +
//                    '}';
//        }
//    }
//
//    private static class StubActionRequest extends ActionRequest {
//
//        private final long requestNum;
//        private final long actionNum;
//
//        private final ExecRecord execRecord;
//
//        public StubActionRequest(long requestNum, long actionNum, long execTime) {
//            super(NoOpClient.class, mock(Action.class), mock(RequestContext.class));
//            this.requestNum = requestNum;
//            this.actionNum = actionNum;
//            this.execRecord = new ExecRecord(execTime);
//        }
//
//        public ExecRecord getExecRecord() {
//            return execRecord;
//        }
//
//        @Override
//        public Void get() throws InterruptedException, ExecutionException {
//            execRecord.setStart(System.currentTimeMillis());
//            return super.get();
//        }
//
//        @Override
//        public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
//            execRecord.setStart(System.currentTimeMillis());
//            return super.get(timeout, unit);
//        }
//
//        @Override
//        public String toString() {
//            return String.format("Action-%d-%d", requestNum, actionNum);
//        }
//    }
//
//    private static class StubAction implements Action<NoOpClient, StubAction> {
//
//        @Override
//        public Class<NoOpClient> acceptsClientType() {
//            return null;
//        }
//
//        @Override
//        public StubAction clone() {
//            return null;
//        }
//
//    }

}
