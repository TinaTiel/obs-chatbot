package com.tinatiel.obsclient;

import com.tinatiel.obsclient.model.request.AbstractObsRequest;

import javax.websocket.Session;
import java.util.concurrent.CompletableFuture;

public interface ObsClient {

    Session getSession();
    CompletableFuture<String> sendMessage(AbstractObsRequest request);

}
