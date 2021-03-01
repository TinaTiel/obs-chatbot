/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.chat.twitch;

import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.error.ClientException;
import com.tinatiel.obschatbot.core.request.queue.ActionCommand;
import org.pircbotx.PircBotX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TwitchChatClientManagerQueueImpl implements StatefulTwitchChatClientManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // State queue is read by the client manager, but written by the client implementations
    private final BlockingQueue<TwitchStateMessage> stateMessages;

    // Client factory produces new client instances
    private final ClientFactory<PircBotX> clientFactory;

    // Listener for firing events
    private final Listener<PircBotX> listener;

    // Executor for thread executing queue polling
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // State management and client reference
    ReadWriteLock rwLock = new ReentrantReadWriteLock();
    Lock readLock = rwLock.readLock();
    private volatile State state = State.STOPPED;
    private PircBotX client;

    public TwitchChatClientManagerQueueImpl(BlockingQueue<TwitchStateMessage> stateMessages, ClientFactory<PircBotX> clientFactory, Listener<PircBotX> listener) {
        this.stateMessages = stateMessages;
        this.clientFactory = clientFactory;
        this.listener = listener;
        consumeQueues();
    }

    private void consumeQueues() {

        // Taking from the queue is blocking until an element is found
        // We don't want this to block the rest of the manager, so we
        // are putting it into its own executor thread.
        executorService.execute(() -> {
            while(true) {
                try {
                    TwitchStateMessage currentState = stateMessages.take();
                    listener.onState(currentState);
                    state = currentState.getState();
                } catch (InterruptedException interruptedException) {
                    log.error("Client manager was interrupted while taking state", interruptedException);
                } finally {
                }
            }
        });

    }

    @Override
    public void startClient() throws ClientException {

    }

    @Override
    public void stopClient() {

    }

    @Override
    public void reloadClient() throws ClientException {

    }

    @Override
    public void consume(ActionCommand actionCommand) {

    }

    @Override
    public State getState() {
        return State.valueOf(state.name());
    }
}
