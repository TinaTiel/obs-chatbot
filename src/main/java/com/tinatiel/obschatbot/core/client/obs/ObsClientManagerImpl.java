/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.obs;

import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.core.error.ClientException;
import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import com.tinatiel.obschatbot.core.request.queue.ActionCommand;
import net.twasi.obsremotejava.OBSRemoteController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ObsClientManagerImpl implements ClientManager<ObsChatbotEvent> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ObsSettings settings;

    CompletableFuture<Void> connected;
    CompletableFuture<Void> disconnected;

    private ObsActionCommandConsumer consumer;
    private OBSRemoteController obsRemoteController;

    public ObsClientManagerImpl(ObsSettings settings) {
        this.settings = settings;
    }

    @Override
    public void consume(ActionCommand actionCommand) throws ClientException {
        if(consumer == null) throw new ClientException("Client has not been initialized yet");
        try{
            consumer.consume(actionCommand);
        } catch (Exception e) {
            log.error("Could not execute actionCommand " + actionCommand, e);
            actionCommand.cancel(true);
        }
    }

    @Override
    public void startClient() {
        if(obsRemoteController == null) {
            // Initialize the client and shared data
            connected = new CompletableFuture<>();
            disconnected = new CompletableFuture<>();
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
                disconnected.complete(null);
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
                throw new ClientException("Execution failed during start", executionException);
            } catch (TimeoutException e) {
                throw new ClientException("OBS Client failed to start within timeout (" + settings.getConnectionTimeoutMs() + "ms)", e);
            }
            consumer = new ObsActionCommandConsumer(obsRemoteController);
        }
    }

    @Override
    public void stopClient() {
        log.info("Stopping OBS Client");
        if(obsRemoteController == null) throw new ClientException("No client was available to stop");
        obsRemoteController.disconnect();
        try {
            disconnected.get(settings.getConnectionTimeoutMs(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException interruptedException) {
            log.error("Disconnection interrupted", interruptedException);
        } catch (ExecutionException executionException) {
            throw new ClientException("Execution failed during stop", executionException);
        } catch (TimeoutException e) {
            throw new ClientException("OBS Client failed to stop within timeout (" + settings.getConnectionTimeoutMs() + "ms)", e);
        } finally {
            // Always dereference the controller so it gets garbage-collected
            obsRemoteController = null;
        }
    }

    @Override
    public void reloadClient() {
        log.info("Reloading OBS Client");
        stopClient();
        startClient();
    }


    @Override
    public void onEvent(ObsChatbotEvent event) {

    }
}
