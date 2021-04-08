package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.messaging.*;
import com.tinatiel.obschatbot.core.request.ActionCompleteEvent;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.CommandRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class RequestSchedulerConfig {

    @Autowired
    QueueClient<ActionRequest> actionRequestQueueClient;

    @Bean
    WorkGroup broadcasterWorkGroup() {
        return new WorkGroupImpl();
    }

    @Bean
    WorkGroup moderatorWorkGroup() {
        return new WorkGroupImpl();
    }

    @Bean
    WorkGroup otherWorkGroup() {
        return new WorkGroupImpl(3);
    }

    @Bean
    BlockingQueue<ActionCompleteEvent> actionCompleteEventQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean
    QueueClient<ActionCompleteEvent> actionCompleteEventQueueClient() {
        return new QueueClientImpl(actionCompleteEventQueue());
    }

    @Bean
    QueueNotifier<ActionCompleteEvent> actionCompleteEventQueueNotifier() {
        QueueNotifier<ActionCompleteEvent> notifier = new QueueNotifierImpl(actionCompleteEventQueue());
        notifier.addListener(broadcasterWorkGroup());
        notifier.addListener(moderatorWorkGroup());
        notifier.addListener(otherWorkGroup());

        return notifier;
    }

    @Bean
    WorkGroupRouter workGroupRouter() {
        return new BroadcasterModeratorElseWorkGroupRouter(
                broadcasterWorkGroup(), moderatorWorkGroup(), otherWorkGroup());
    }

    @Bean
    WorkGroupManager workGroupManager() {
        return new WorkGroupManagerImpl(workGroupRouter());
    }

    @Bean
    Listener<CommandRequest> commandRequestScheduler() {
        return new CommandRequestScheduler(workGroupManager(), actionRequestQueueClient);
    }

}