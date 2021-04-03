package com.tinatiel.obschatbot.core.request;

import com.tinatiel.obschatbot.core.messaging.*;
import com.tinatiel.obschatbot.core.request.scheduler.CommandRequestScheduler;
import com.tinatiel.obschatbot.core.request.scheduler.WorkGroupManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class QueueConfig {

    // ====== CommandRequest Queue ====== //
    @Bean
    BlockingQueue<CommandRequest> commandRequestQueue() {
        return new LinkedBlockingQueue<CommandRequest>();
    }

    @Bean
    QueueClient<CommandRequest> commandRequestQueueClient() {
        return new QueueClientImpl(commandRequestQueue());
    }

    // ====== ActionRequest Queue ====== //
    @Bean
    BlockingQueue<ActionRequest> actionRequestQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean
    QueueClient<ActionRequest> actionRequestQueueClient() {
        return new QueueClientImpl(actionRequestQueue());
    }

}
