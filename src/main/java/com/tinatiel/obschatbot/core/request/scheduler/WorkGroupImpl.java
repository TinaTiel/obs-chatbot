package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.request.ActionCompleteEvent;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.CommandRequest;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class WorkGroupImpl implements WorkGroup {

    private final Timer timer = new Timer();
    private final List<CommandRequestWrapper> workableRequests = new ArrayList<>();
    private final HashMap<UUID, CommandRequestWrapper> blockedRequests = new HashMap<>();
    private ReentrantLock lock = new ReentrantLock();

    @Override
    public void add(CommandRequest commandRequest) {
        workableRequests.add(new CommandRequestWrapper(commandRequest));
    }

    @Override
    public void free(UUID actionRequestId) {
        lock.lock();
        try {

            // Get the request. If it doesn't exist, then exit
            CommandRequestWrapper blockedRequest = blockedRequests.get(actionRequestId);
            if(blockedRequest == null) return;

            // If the request has work to do, then add it back to workable requests
            if(!blockedRequest.getQueue().isEmpty()) workableRequests.add(blockedRequest);

            // Remove the request from the blocked requests
            blockedRequests.remove(actionRequestId);

        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<ActionRequest> getNextWorkBatch() {

        List<ActionRequest> batch = new ArrayList<>();
        List<CommandRequestWrapper> removeNext = new ArrayList<>();

        // Process the workable requests
        lock.lock();
        try {
            workableRequests.forEach( request -> {

                // Get the next action
                ActionRequest nextAction = request.getQueue().poll();

                if(nextAction != null) {
                    // If there is an action, then add it to the batch
                    batch.add(nextAction);

                    if(nextAction.requiresCompletion()) {
                        // Actions requiring completion are added to the
                        // block list and will be removed from workable requests
                        blockedRequests.put(
                                nextAction.getId(),
                                request
                        );
                        removeNext.add(request);

                        // We also must timeout the task if it takes too long so
                        // it doesn't block forever
                        long timeout = nextAction.getAction().getTimeout() < 0 ?
                                1 : nextAction.getAction().getTimeout();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                free(nextAction.getId());
                            }
                        }, timeout);
                    }

                }

                // If there is nothing left in the queue, then mark the request for removal
                if(request.getQueue().isEmpty()){
                    // If there is no action, then the request is empty
                    removeNext.add(request);
                }

            });

            // Remove non-workable items from the workable requests
            removeNext.forEach(workableRequests::remove);
        } finally {
            lock.unlock();
        }

        return batch;
    }

    @Override
    public int getNumberOfInflightRequests() {
        lock.lock();
        int result;
        try{
            result = workableRequests.size() + blockedRequests.size();
        } finally {
            lock.unlock();
        }
        return result;
    }

    @Override
    public int getNumberOfWorkableRequests() {
        int result;
        lock.lock();
        try{
            result = workableRequests.size();
        } finally {
            lock.unlock();
        }
        return result;
    }

    @Override
    public void onEvent(ActionCompleteEvent event) {
        free(event.getCompletedActionRequestId());
    }

    private static class CommandRequestWrapper {

        private final CommandRequest request;
        private final Queue<ActionRequest> actionRequestQueue = new LinkedList<>();

        public CommandRequestWrapper(CommandRequest request) {
            this.request = request;
            this.actionRequestQueue.addAll(request.getActionCommands());
        }

        public Queue<ActionRequest> getQueue() {
            return actionRequestQueue;
        }

    }

}
