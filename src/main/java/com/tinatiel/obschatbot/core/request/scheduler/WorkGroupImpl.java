package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.CommandRequest;

import java.util.*;

public class WorkGroupImpl implements WorkGroup {

    private final List<CommandRequestWrapper> workableRequests = new ArrayList<>();
    private final HashMap<UUID, CommandRequestWrapper> blockedRequests = new HashMap<>();

    @Override
    public void add(CommandRequest commandRequest) {
        workableRequests.add(new CommandRequestWrapper(commandRequest));
    }

    @Override
    public void free(ActionRequest actionRequest) {
        CommandRequestWrapper blockedRequest = blockedRequests.get(actionRequest.getId());
        if(blockedRequest == null) return;

        workableRequests.add(blockedRequest);
        blockedRequests.remove(actionRequest.getId());
    }

    @Override
    public List<ActionRequest> getNextWorkBatch() {

        List<ActionRequest> batch = new ArrayList<>();
        List<CommandRequestWrapper> removeNext = new ArrayList<>();

        // Process the workable requests
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

        return batch;
    }

    @Override
    public int getNumberOfInflightRequests() {
        return workableRequests.size() + blockedRequests.size();
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
