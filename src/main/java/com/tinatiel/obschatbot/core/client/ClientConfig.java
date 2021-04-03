package com.tinatiel.obschatbot.core.client;

import com.tinatiel.obschatbot.core.messaging.Listener;
import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import com.tinatiel.obschatbot.core.messaging.QueueNotifier;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;

@Configuration
public class ClientConfig {

    @Bean
    Listener<ObsChatbotEvent> eventLogger() {
        return new Listener<ObsChatbotEvent>() {

            private final Logger log = LoggerFactory.getLogger(this.getClass());

            @Override
            public void onEvent(ObsChatbotEvent event) {
                log.debug("Logged Event: " + event.toString());
            }
        };
    }

}
