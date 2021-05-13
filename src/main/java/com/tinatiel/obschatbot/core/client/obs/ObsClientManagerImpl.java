/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.obs;

import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.client.ActionCommandConsumer;
import com.tinatiel.obschatbot.core.client.ClientDelegate;
import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.core.client.event.ClientAuthenticatingEvent;
import com.tinatiel.obschatbot.core.client.event.ClientConnectedEvent;
import com.tinatiel.obschatbot.core.client.event.ClientConnectingEvent;
import com.tinatiel.obschatbot.core.client.event.ClientDisconnectedEvent;
import com.tinatiel.obschatbot.core.client.event.ClientErrorEvent;
import com.tinatiel.obschatbot.core.client.event.ClientReadyEvent;
import com.tinatiel.obschatbot.core.client.event.ClientRequestIgnoredEvent;
import com.tinatiel.obschatbot.core.client.event.ClientStartRequestedEvent;
import com.tinatiel.obschatbot.core.client.event.ClientStartingEvent;
import com.tinatiel.obschatbot.core.client.event.ClientStopRequestedEvent;
import com.tinatiel.obschatbot.core.client.event.ClientStoppedEvent;
import com.tinatiel.obschatbot.core.client.event.ClientStoppingEvent;
import com.tinatiel.obschatbot.core.client.obs.messaging.ObsClientLifecycleGateway;
import com.tinatiel.obschatbot.core.client.twitch.chat.TwitchChatClientDelegate;
import com.tinatiel.obschatbot.core.client.twitch.chat.TwitchChatClientSettings;
import com.tinatiel.obschatbot.core.error.ClientException;
import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.user.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import net.twasi.obsremotejava.OBSRemoteController;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
@Slf4j
public class ObsClientManagerImpl implements ClientManager {

  private final ObsClientLifecycleGateway lifecycleGateway;
  private final ClientFactory<OBSRemoteController, ObsClientSettings> clientFactory;
  private final ActionCommandConsumer<ObsClientDelegate> consumer;

  ExecutorService executorService = Executors.newSingleThreadExecutor();
  private volatile ObsClientDelegate clientDelegate;
  private volatile boolean ready;

  public ObsClientManagerImpl(
    ObsClientLifecycleGateway lifecycleGateway,
    ClientFactory<OBSRemoteController, ObsClientSettings> clientFactory,
    ActionCommandConsumer<ObsClientDelegate> consumer) {
    this.lifecycleGateway = lifecycleGateway;
    this.clientFactory = clientFactory;
    this.consumer = consumer;
  }

  @Override
  public void startClient() {

    // Acknowledge the start request
    lifecycleGateway.submit(new ClientStartRequestedEvent());

    // Check the clientDelegate hasn't already been generated. If it has, then ignore request.
    if (clientDelegate != null) {
      lifecycleGateway
        .submit(new ClientRequestIgnoredEvent("Client already starting/started, ignoring"));
      return;
    }

    // Acknowledge the start will occur
    lifecycleGateway.submit(new ClientStartingEvent());

    // Generate a new client delegate, checking it's the type we want
    ClientDelegate<OBSRemoteController, ObsClientSettings> generatedDelegate = clientFactory.generate();
    if (generatedDelegate instanceof ObsClientDelegate) {
      clientDelegate = (ObsClientDelegate) generatedDelegate;
    } else {
      // this should never happen during runtime; this is a developer issue!
      throw new IllegalStateException(
        "Client factory did not return expected delegate. Was: " + generatedDelegate);
    }

    // Acknowledge the client will now start connecting
    lifecycleGateway.submit(new ClientConnectingEvent());
    if(clientDelegate.getSettings().getPassword() != null) lifecycleGateway.submit(new ClientAuthenticatingEvent());

    // Start the client, isolating the process in its own thread so it cannot block main
    executorService.execute(() -> {
      try {
        clientDelegate.getClient().connect();
      } catch (Exception e) {
        lifecycleGateway.submit(new ClientErrorEvent(
          "OBS client encountered an unexpected error during start/run: "
            + e.getMessage(), e
        ));
      }
    });

  }

  @Override
  public void stopClient() {
    privateStop("Stop request by user");
  }

  @Override
  public void reloadClient() {
    stopClient();
    startClient();
  }

  @ServiceActivator(inputChannel = "obsClientLifecycleChannel")
  @Override
  public void onLifecycleEvent(ObsChatbotEvent event) {
    log.info("Obs Client Manager Event: " + event);
    // Check if the client is already stopped; we don't want an unexpected error to cause
    // the client to leave the stopped state, e.g. due to underlying timeout of the wss client
    if(!ready && event instanceof ClientErrorEvent) {
      log.error("Unexpected event while stopped: " + event);
      return;
    }

    if (event instanceof ClientErrorEvent) {
      ClientErrorEvent castEvent = (ClientErrorEvent) event;

      // Check specifically for not connecting to OBS due to it not running; the client doesn't
      // emit any further events, so we must initiate the stop process.
      if(castEvent.getMessage() != null
        && castEvent.getMessage().toLowerCase().contains("websocket error")) {
//        lifecycleGateway.submit(new ClientStopRequestedEvent(castEvent.getMessage()));
//        lifecycleGateway.submit(new ClientDisconnectedEvent(castEvent.getMessage()));
      } else {
        // Any error event must stop the client; something is wrong
        privateStop("Stopping due to error: " + castEvent.getMessage());
      }

    } else if (event instanceof ClientConnectedEvent) {
      lifecycleGateway.submit(new ClientReadyEvent());

    } else if (event instanceof ClientDisconnectedEvent) {
      // When disconnected, we know the client is safely disengaged, so
      // we can clear it out and send a Stopped event
      lifecycleGateway.submit(new ClientStoppedEvent());
      clientDelegate = null;
    } else if (event instanceof ClientReadyEvent) {
      ready = true;
    }

    log.debug("Ready: " + ready);

  }

  @ServiceActivator(inputChannel = "actionRequestChannel")
  @Override
  public void onActionRequest(ActionRequest actionRequest) throws ClientException {
    if (ready) {
      consumer.consume(clientDelegate, actionRequest);
    } else {
      lifecycleGateway.submit(new ClientRequestIgnoredEvent("Ignoring request "
        + actionRequest + ": Client not ready"));
    }
  }

  private void privateStop(String stopReason) {

    // Acknowledge a stop was requested
    lifecycleGateway.submit(new ClientStopRequestedEvent(stopReason));

    // Stop the client if present, else ignore the request
    if (clientDelegate == null) {
      lifecycleGateway.submit(new ClientRequestIgnoredEvent("Stop ignored: Client wasn't running"));
      return;
    }

    // Acknowledge the request will now result in a stop
    lifecycleGateway.submit(new ClientStoppingEvent());

    // Stop the client
    clientDelegate.getClient().disconnect();

    // Note the rest of the close process will be completed in the OBS Remote callbacks!

  }

}
