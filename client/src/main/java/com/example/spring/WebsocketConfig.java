package com.example.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.standard.AnnotatedEndpointConnectionManager;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@EnableWebSocket
@Configuration
public class WebsocketConfig {

    private final String endpoint = "ws://{host}:4444/";

    @Bean
    public AnnotatedEndpointConnectionManager annotatedEndpointConnectionManager() {
        AnnotatedEndpointConnectionManager manager = new AnnotatedEndpointConnectionManager(client(), endpoint, "localhost");
        return manager;
    }

    @Bean
    public ExampleSpringClient client() {
        return new ExampleSpringClientImpl();
    }

}
