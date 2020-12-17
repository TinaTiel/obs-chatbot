package com.tinatiel.obsclient.example.spring;

import javax.websocket.*;

@ClientEndpoint(encoders = {GetVersionEncoder.class})
public class ExampleSpringClientImpl implements ExampleSpringClient {

    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to " + session.getId()
                + ", with Session ID " + session.getId());
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Client ID " + session.getId() + " Received message: " + message);
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Session closed");
        this.session = null;
    }

    @OnError
    public void onError(Session session, Throwable exception) {
        System.out.println("On Error, exception: " + exception);
        exception.printStackTrace();
    }

    public void sendSomeMessage(String yourMessage) {
        System.out.println("Client ID " + session.getId() + " sending message: " + yourMessage);
        session.getAsyncRemote().sendText(yourMessage);
    }

    public Session getSession() {
        return session;
    }

}
