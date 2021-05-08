package com.tinatiel.obschatbot.core.request;

import com.tinatiel.obschatbot.core.messaging.Listener;
import com.tinatiel.obschatbot.core.messaging.QueueNotifier;
import com.tinatiel.obschatbot.core.messaging.QueueNotifierImpl;
import com.tinatiel.obschatbot.core.request.scheduler.RequestSchedulerConfig;
import java.util.concurrent.BlockingQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Encompasses configuration for QueueNotifier instances.
 */
@Import({QueueConfig.class, RequestSchedulerConfig.class})
@Configuration
public class QueueNotifierConfig {

//  @Autowired
//  BlockingQueue<CommandRequest> commandRequestQueue;
//
//  @Autowired
//  BlockingQueue<ActionRequest> actionRequestQueue;
//
//  @Autowired
//  Listener<CommandRequest> commandRequestScheduler;
//
//  @Autowired
//  Listener<ActionRequest> twitchChatActionRequestListener;
//
//  @Bean
//  QueueNotifier<CommandRequest> commandRequestQueueNotifier() {
//    QueueNotifier<CommandRequest> notifier = new QueueNotifierImpl(commandRequestQueue);
//    notifier.addListener(commandRequestScheduler);
//
//    return notifier;
//  }
//
//  @Bean
//  QueueNotifier<ActionRequest> actionRequestQueueNotifier() {
//    QueueNotifier<ActionRequest> notifier = new QueueNotifierImpl(actionRequestQueue);
//    notifier.addListener(twitchChatActionRequestListener);
//
//    return notifier;
//  }

}
