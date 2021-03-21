/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.chat.twitch;

import com.tinatiel.obschatbot.core.client.*;
import com.tinatiel.obschatbot.core.error.ClientException;
import com.tinatiel.obschatbot.core.messaging.QueueClient;
import com.tinatiel.obschatbot.core.request.queue.ActionCommand;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class ClientManagerTwitchChatImpl implements ClientManager<TwitchClientStateEvent> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Client factory produces new client instances
    private final QueueClient<TwitchClientStateEvent> stateClient;
    private final ClientFactory<PircBotX> clientFactory;

    private final ReentrantLock lock = new ReentrantLock();
    private volatile PircBotX client;
    private TwitchClientState state;

    public ClientManagerTwitchChatImpl(QueueClient<TwitchClientStateEvent> stateClient, ClientFactory<PircBotX> clientFactory) {
        this.stateClient = stateClient;
        this.clientFactory = clientFactory;
    }

    @Override
    public void startClient() throws ClientException {

        if(client == null) {
            stateClient.submit(new TwitchClientStateEvent(TwitchClientState.START_REQUESTED));
        } else {
            stateClient.submit(new TwitchClientStateEvent(TwitchClientState.START_REQUESTED, "Client already starting/started, ignoring"));
            return;
        }

        stateClient.submit(new TwitchClientStateEvent(TwitchClientState.STARTING));
        client = clientFactory.generate();
        stateClient.submit(new TwitchClientStateEvent(TwitchClientState.CONNECTING));

        // startBot blocks the calling thread, so we're putting it in its own executor thread
        executorService.execute(() -> {
            try {
                client.startBot();
            } catch (IOException | IrcException e) {
                log.error("Unable to start the Twitch Client", e);
                stateClient.submit(new TwitchClientStateEvent(TwitchClientState.ERROR, "Unable to start the Twitch Client: "
                        + e.getMessage()
                ));
            }
        });

    }

    @Override
    public void stopClient() {
        privateStop("Stop request by user");
    }

    private void privateStop(String message) {

        // Stop the client if present
        if(client == null) {
            stateClient.submit(new TwitchClientStateEvent(TwitchClientState.STOP_REQUESTED, "Client wasn't running, ignoring"));
            return;
        } else {
            stateClient.submit(new TwitchClientStateEvent(TwitchClientState.STOP_REQUESTED, message));
        }

        stateClient.submit(new TwitchClientStateEvent(TwitchClientState.STOPPING));
        client.stopBotReconnect();

        // Try to close the connection
        try {
            client.close();
        } catch (Exception e) {
            log.warn("Could not close the connection", e);
        }

        // Note the rest of the close process will be completed in the listener!

    }

    @Override
    public void reloadClient() throws ClientException {
        stopClient();
        startClient();
    }

    @Override
    public void consume(ActionCommand actionCommand) {

    }

    @Override
    public void onEvent(TwitchClientStateEvent event) {
        state = event.getState();
        switch (state) {
            case ERROR:
                privateStop("Stopping due to error: " + event.getMessage());
                break;
            case DISCONNECTED:
                stateClient.submit(new TwitchClientStateEvent(TwitchClientState.STOPPED));
                client = null;
        }
    }

}
