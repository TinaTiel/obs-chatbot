/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.obs;

import com.tinatiel.obschatbot.core.error.ClientNotAvailableException;
import com.tinatiel.obschatbot.core.request.queue.ActionCommand;
import net.twasi.obsremotejava.OBSRemoteController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ObsClientManagerImpl implements ObsClientManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ObsSettings settings;

    private final Lock connectedLock = new ReentrantLock();
    private final Condition connectedCondition = connectedLock.newCondition();

    private ObsActionCommandConsumer consumer;
    private OBSRemoteController obsRemoteController;

    public ObsClientManagerImpl(ObsSettings settings) {
        this.settings = settings;
    }

    @Override
    public void consume(ActionCommand actionCommand) throws ClientNotAvailableException {
        if(consumer == null) throw new ClientNotAvailableException("Client has not been initialized yet");
        try{
            consumer.consume(actionCommand);
        } catch (Exception e) {
            log.error("Could not execute actionCommand " + actionCommand, e);
            actionCommand.cancel(true);
        }
    }

    @Override
    public void start() {
        if(obsRemoteController == null) {
            // Initialize the client
            CompletableFuture<Void> connected = new CompletableFuture<>();
            obsRemoteController = new OBSRemoteController(
                    "ws://" + settings.getHost() + ":" + settings.getPort(),
                    false,
                    settings.getPassword(),
                    false
            );

            // Define callbacks, we want to complete the future on connection
            obsRemoteController.registerOnError((message, throwable) -> {
                log.error("OBS Client encountered an error: " + message, throwable);
            });
            obsRemoteController.registerCloseCallback((statusCode, reason) -> {
                log.info(String.format("OBS Client closed with statusCode %s and reason '%s", statusCode, reason));
                obsRemoteController = null;
            });
            obsRemoteController.registerConnectionFailedCallback(message -> {
                log.error("Connection to OBS failed: " + message);
            });
            obsRemoteController.registerConnectCallback(response -> {
                connected.complete(null);
            });

            // Connect
            obsRemoteController.connect();

            // Await connection completion, waiting up to the timeout
            try {
                connected.get(settings.getConnectionTimeoutMs(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException interruptedException) {
                log.error("Connection interrupted", interruptedException);
            } catch (ExecutionException executionException) {
                throw new ClientNotAvailableException("Execution failed during start", executionException);
            } catch (TimeoutException e) {
                throw new ClientNotAvailableException("OBS Client failed to start within timeout (" + settings.getConnectionTimeoutMs() + "ms)", e);
            }
            consumer = new ObsActionCommandConsumer(obsRemoteController);
        }
    }

    @Override
    public void stop() {
        log.info("Stopping OBS Client");
        if(obsRemoteController == null) throw new ClientNotAvailableException("No client was available to stop");
        obsRemoteController.disconnect();
    }

    @Override
    public void reload() {
        log.info("Reloading OBS Client");
        stop();
        start();
    }

    @Override
    public OBSRemoteController getClient() {
        if(obsRemoteController == null) throw new ClientNotAvailableException("Client not yet available");
        return obsRemoteController;
    }

}
