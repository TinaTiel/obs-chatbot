package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.messaging.Listener;
import com.tinatiel.obschatbot.core.request.CommandRequest;
import com.tinatiel.obschatbot.core.request.messaging.ActionRequestGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Encompasses all configuration for the Request Scheduler (work groups, etc.).
 */
@Configuration
public class RequestSchedulerConfig {

  @Autowired
  ActionRequestGateway actionRequestGateway;

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
    return new CommandRequestScheduler(workGroupManager(), actionRequestGateway);
  }

}
