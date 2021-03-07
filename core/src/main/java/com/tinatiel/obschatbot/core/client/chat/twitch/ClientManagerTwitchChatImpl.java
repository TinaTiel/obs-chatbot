/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.chat.twitch;

import com.tinatiel.obschatbot.core.client.*;
import com.tinatiel.obschatbot.core.error.ClientException;
import com.tinatiel.obschatbot.core.request.queue.ActionCommand;
import org.pircbotx.PircBotX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ClientManagerTwitchChatImpl implements ClientManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // Client factory produces new client instances
    private final ClientFactory<PircBotX> clientFactory;

    private PircBotX client;

    public ClientManagerTwitchChatImpl(ClientFactory<PircBotX> clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public void startClient() throws ClientException {

    }

    @Override
    public void stopClient() {
        client.stopBotReconnect();
        client.close();
        client = null;
    }

    @Override
    public void reloadClient() throws ClientException {
        stopClient();
        startClient();
    }

    @Override
    public void onEvent(StateEvent event) {

    }

    @Override
    public void consume(ActionCommand actionCommand) {

    }
}
