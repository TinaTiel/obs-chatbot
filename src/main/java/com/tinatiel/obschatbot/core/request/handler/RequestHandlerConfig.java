package com.tinatiel.obschatbot.core.request.handler;

import com.tinatiel.obschatbot.core.messaging.QueueClient;
import com.tinatiel.obschatbot.core.request.CommandRequest;
import com.tinatiel.obschatbot.core.request.factory.RequestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// TODO: Get rid of this, it adds very strange/confusing dependency patterns
@Configuration
public class RequestHandlerConfig {

    @Autowired
    RequestFactory requestFactory;

    @Autowired
    QueueClient<CommandRequest> commandRequestQueueClient;

    @Bean
    CommandRequestDispatcher commandRequestDispatcher() {
        return new CommandRequestDispatcherImpl(
                requestFactory,
                commandRequestQueueClient
        );
    }

}
