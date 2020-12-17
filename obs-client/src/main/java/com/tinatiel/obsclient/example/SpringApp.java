package com.tinatiel.obsclient.example;

import com.tinatiel.obsclient.example.spring.ExampleSpringClient;
import com.tinatiel.obsclient.example.spring.GetVersion;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.socket.client.standard.AnnotatedEndpointConnectionManager;

@SpringBootApplication
public class SpringApp {
    public static void main(String[] args) {

        ApplicationContext ctx = SpringApplication.run(SpringApp.class, args);

        // Get the connection manager and start the WS client (instead of doing it directly)
        AnnotatedEndpointConnectionManager manager = ctx.getBean(AnnotatedEndpointConnectionManager.class);
        manager.start();

        try {
            System.out.println("Waiting for initialization to finish...");
            Thread.sleep(5000); // We need to wait for the client to initialize so we keep it simple in this example and wait.
        } catch (InterruptedException ignored) {}
        ExampleSpringClient client = ctx.getBean(ExampleSpringClient.class);

        // Send some messages
        System.out.println("Making client calls");
//        client.sendSomeMessage("{\"request-type\": \"GetVersion\",\"message-id\":\"" + client.getSession().getId() + "\"}" );
//        client.sendSomeMessage("{\"request-type\": \"GetVersion\",\"message-id\":\"foobarbaz\"}");
//        client.sendSomeMessage("{\"request-type\": \"BroadcastCustomMessage\",\"message-id\":\"" + client.getSession().getId() + "\",\"realm\": \"myCustomRealm\",\"data\": {\"foo\":\"bar\"} }" );
        client.getSession().getAsyncRemote().sendObject(new GetVersion(client.getSession().getId()));
    }
}
