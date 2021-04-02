package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.messaging.Listener;
import com.tinatiel.obschatbot.core.messaging.QueueClient;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.CommandRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandRequestScheduler implements Listener<CommandRequest> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final WorkGroupManager workGroupManager;
    private final QueueClient<ActionRequest> actionRequestQueueClient;

    public CommandRequestScheduler(WorkGroupManager workGroupManager, QueueClient<ActionRequest> actionRequestQueueClient) {
        this.workGroupManager = workGroupManager;
        this.actionRequestQueueClient = actionRequestQueueClient;
        execute();
    }

    @Override
    public void onEvent(CommandRequest event) {
        workGroupManager.route(event);
    }

    private void execute() {
        executorService.execute(() -> {
            while(true) {
                List<ActionRequest> actionRequests = workGroupManager.getNext().getNextWorkBatch();
                actionRequests.forEach(actionRequestQueueClient::submit);
                if(actionRequests.isEmpty()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        log.error("CommandRequestScheduler was interrupted", e);
                    }
                }
            }
        });
    }
}
