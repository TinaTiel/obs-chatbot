package com.tinatiel.obsclient.example.spring;

import javax.websocket.Session;

public interface ExampleSpringClient {

    void sendSomeMessage(String yourMessage);
    public Session getSession();
}
