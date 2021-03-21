/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.chat.twitch;

import com.tinatiel.obschatbot.core.messaging.QueueClient;
import com.tinatiel.obschatbot.core.request.queue.ActionCommand;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PircBotxListener extends ListenerAdapter {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final QueueClient<TwitchClientEvent> stateClient;
    private final QueueClient<ActionCommand> requestClient;

    public PircBotxListener(QueueClient<TwitchClientEvent> stateClient, QueueClient<ActionCommand> requestClient) {
        this.stateClient = stateClient;
        this.requestClient = requestClient;
    }

    /**
     * When we have successfully reached Twitch's server.
     */
    @Override
    public void onSocketConnect(SocketConnectEvent event) throws Exception {
        stateClient.submit(new TwitchClientEvent(TwitchClientState.CONNECTED, event.toString()));
    }

    /**
     * When we have successfully reached the server, AND successfully authenticated.
     */
    @Override
    public void onConnect(ConnectEvent event) throws Exception {
        stateClient.submit(new TwitchClientEvent(TwitchClientState.AUTHENTICATED, event.toString()));
    }

    @Override
    public void onJoin(JoinEvent event) throws Exception { // joining the channel

        // Request tags capability so we can determine if a mod, subscriber, etc.
        stateClient.submit(new TwitchClientEvent(TwitchClientState.JOINING, "Requesting Tags capability"));
        event.getBot().sendCAP().request("twitch.tv/tags");

        // Request commands capability so we can respond to RECONNECT if issued by Twitch IRC server
        stateClient.submit(new TwitchClientEvent(TwitchClientState.JOINING, "Requesting Commands capability"));
        event.getBot().sendCAP().request("twitch.tv/commands");

        // Drop a welcome message into chat to provide feedback to broadcaster
        event.getBot().sendIRC().message("#tinatiel", "Obs Chatbot has joined the chat!");

        stateClient.submit(new TwitchClientEvent(TwitchClientState.JOINED, event.toString()));
        stateClient.submit(new TwitchClientEvent(TwitchClientState.READY, event.toString()));

    }

    @Override
    public void onNotice(NoticeEvent event) throws Exception {
        if(event.getNotice().contains("auth")) {
            stateClient.submit(new TwitchClientEvent(TwitchClientState.ERROR, "Unable to connect to Twitch: bad credentials"));
        }
    }

    @Override
    public void onException(ExceptionEvent event) throws Exception {
        stateClient.submit(new TwitchClientEvent(TwitchClientState.ERROR, event.getException().getMessage()));
    }

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        log.debug("ON MESSAGE event: " + event);
        log.info("Received Message from user '" + event.getUser().getNick() + "' : '" + event.getMessage() + "'");
        log.info("Tags: " + event.getTags());
    }

    @Override
    public void onDisconnect(DisconnectEvent event) throws Exception {
        stateClient.submit(new TwitchClientEvent(TwitchClientState.DISCONNECTED));
    }

    @Override
    public void onEvent(Event event) throws Exception {
        log.debug("EVENT: " + event);
        super.onEvent(event);
    }

    @Override
    public void onOutput(OutputEvent event) throws Exception {
        if(event.getRawLine().startsWith("PASS")) {
            stateClient.submit(new TwitchClientEvent(TwitchClientState.AUTHENTICATING));
        }
        super.onOutput(event);
    }

    @Override
    public void onConnectAttemptFailed(ConnectAttemptFailedEvent event) throws Exception {
        stateClient.submit(new TwitchClientEvent(TwitchClientState.ERROR, "Could not connect to Twitch, "
            + "your (1) network connection, (2) that Twitch is up, and (3) verify the port and host are correct"
        ));
    }
}
