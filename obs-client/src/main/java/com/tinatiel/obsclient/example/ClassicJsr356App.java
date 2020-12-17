package com.tinatiel.obsclient.example;

import com.tinatiel.obsclient.example.classic.ExampleClassicClient;

public class ClassicJsr356App {
    public static void main(String[] args) {
        //String endpoint = "wss://echo.websocket.org/";
        String endpoint = "ws://localhost:4444/";
        try {
            // client in this com.example connects in the constructor instead of here
            ExampleClassicClient exampleClassicClient = new ExampleClassicClient(endpoint);
            ExampleClassicClient badPracticeClient = new ExampleClassicClient(endpoint);
            ExampleClassicClient broadcastClient = new ExampleClassicClient(endpoint);
            exampleClassicClient.sendSomeMessage("{\"request-type\": \"GetVersion\",\"message-id\":\"" + exampleClassicClient.getSession().getId() + "\"}" );
            badPracticeClient.sendSomeMessage("{\"request-type\": \"GetVersion\",\"message-id\":\"foobarbaz\"}");
            broadcastClient.sendSomeMessage("{\"request-type\": \"BroadcastCustomMessage\",\"message-id\":\"" + broadcastClient.getSession().getId() + "\",\"realm\": \"myCustomRealm\",\"data\": {\"foo\":\"bar\"} }" );

            // We need to keep the program running to we can receive a response
            // Otherwise it will exit before we get a response
            System.out.println("Press ENTER key to exit.");
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
