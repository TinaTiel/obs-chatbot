package com.tinatiel.obsclient.client;

import com.tinatiel.obsclient.model.ObsRequestEncoder;
import com.tinatiel.obsclient.model.request.AbstractObsRequest;

import javax.websocket.*;
import java.util.concurrent.CompletableFuture;

@ClientEndpoint(encoders = {ObsRequestEncoder.class})
public class ObsClientImpl implements ObsClient {

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

    public Session getSession() {
        return session;
    }

    @Override
    public CompletableFuture<String> sendMessage(AbstractObsRequest request) {
        return null;
    }

}
