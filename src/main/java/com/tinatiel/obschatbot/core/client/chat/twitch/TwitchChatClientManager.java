/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.chat.twitch;

import com.tinatiel.obschatbot.core.ClientInstanceWrapper;
import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.core.client.event.*;
import com.tinatiel.obschatbot.core.error.ClientException;
import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import com.tinatiel.obschatbot.core.messaging.QueueClient;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TwitchChatClientManager implements ClientManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Client factory produces new client instances
    private final QueueClient<ObsChatbotEvent> stateClient;
    private final ClientFactory<PircBotX, TwitchChatClientSettings> clientFactory;

    private volatile PircBotX client;
    private volatile TwitchChatClientSettings settings;
    private volatile ObsChatbotEvent lastEvent;

    public TwitchChatClientManager(QueueClient<ObsChatbotEvent> stateClient,
                                   ClientFactory<PircBotX, TwitchChatClientSettings> clientFactory) {
        this.stateClient = stateClient;
        this.clientFactory = clientFactory;
    }

    @Override
    public void startClient() throws ClientException {

        stateClient.submit(new ClientStartRequestedEvent());

        if(client != null) {
            stateClient.submit(new ClientRequestIgnoredEvent("Client already starting/started, ignoring"));
            return;
        }

        stateClient.submit(new ClientStartingEvent());
        generateClient();
        stateClient.submit(new ClientConnectingEvent());

        // startBot blocks the calling thread, so we're putting it in its own executor thread
        executorService.execute(() -> {
            try {
                client.startBot();
            } catch (IOException | IrcException e) {
                log.error("Twitch client encountered an unexpected error during start/run", e);
                stateClient.submit(new ClientErrorEvent(e, "Twitch client encountered an unexpected error during start/run: "
                        + e.getMessage()
                ));
            }
        });

    }

    @Override
    public void stopClient() {
        privateStop("Stop request by user");
    }

    @Override
    public void reloadClient() throws ClientException {
        stopClient();
        startClient();
    }

    @Override
    public void consume(ActionRequest actionRequest) {
        if(lastEvent instanceof ClientReadyEvent) {
            Action action = actionRequest.getAction();
            if(action instanceof SendMessageAction) {
                client.sendIRC().message(
                        "#" + settings.getBroadcasterChannel(),
                        ((SendMessageAction) action).getMessage()
                );
            }
        } else {
            stateClient.submit(new ClientRequestIgnoredEvent("Ignoring request "
                    + actionRequest + ": Client not ready"));
        }

    }

    @Override
    public void onEvent(ObsChatbotEvent event) {
        lastEvent = event;
        if(event instanceof ClientErrorEvent) {
            // Any error event must stop the client; something is wrong
            privateStop("Stopping due to error: " + ((ClientErrorEvent) event).getMessage());
        } else if (event instanceof ClientDisconnectedEvent) {
            // When disconnected, we know the client is safely disengaged, so
            // we can clear it out and send a Stopped event
            stateClient.submit(new ClientStoppedEvent());
            client = null;
        }
    }

    private void privateStop(String stopReason) {

        // Stop the client if present, else exit
        stateClient.submit(new ClientStopRequestedEvent(stopReason));
        if(client == null) {
            stateClient.submit(new ClientRequestIgnoredEvent("Stop ignored: Client wasn't running"));
            return;
        }

        stateClient.submit(new ClientStoppingEvent());
        client.stopBotReconnect();

        // Try to close the connection
        try {
            client.close();
        } catch (Exception e) {
            log.warn("Could not close the connection", e);
        }

        // Note the rest of the close process will be completed in the listener!

    }

    private void generateClient() {
        ClientInstanceWrapper<PircBotX, TwitchChatClientSettings> wrapper = clientFactory.generate();
        client = wrapper.getClientInstance();
        settings = wrapper.getSettings();
    }


}
