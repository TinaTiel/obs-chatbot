package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.messaging.Listener;
import com.tinatiel.obschatbot.core.messaging.QueueClient;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.CommandRequest;

public class CommandRequestScheduler implements Listener<CommandRequest> {

    private final QueueClient<ActionRequest> actionRequestQueueClient;

    public CommandRequestScheduler(QueueClient<ActionRequest> actionRequestQueueClient) {
        this.actionRequestQueueClient = actionRequestQueueClient;
    }

    @Override
    public void onEvent(CommandRequest event) {

    }
}
