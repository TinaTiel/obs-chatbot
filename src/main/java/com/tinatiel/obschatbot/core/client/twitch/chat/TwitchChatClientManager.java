/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.twitch.chat;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.client.ActionCommandConsumer;
import com.tinatiel.obschatbot.core.client.ClientDelegate;
import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.client.ClientManager;
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
import com.tinatiel.obschatbot.core.client.twitch.chat.messaging.TwitchClientStateMessagingGateway;
import com.tinatiel.obschatbot.core.error.ClientException;
import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.user.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;

/**
 * Implementation of ClientManager that manages the Twitch (IRC) chat client, using an event queue
 * to communicate with the PircBotX listener during lifecycle changes. As an
 * ${@link ActionCommandConsumer}, this implementation also accepts ActionRequests related to chat
 * commands if in the ready state.
 */
public class TwitchChatClientManager implements ClientManager {

  private final Logger log = LoggerFactory.getLogger(this.getClass());
  // Client factory produces new client instances
  private final TwitchClientStateMessagingGateway stateClient;
  private final ClientFactory<PircBotX, TwitchChatClientSettings> clientFactory;
  ExecutorService executorService = Executors.newSingleThreadExecutor();
  private volatile TwitchChatClientDelegate clientDelegate;
  private volatile ObsChatbotEvent lastEvent;

  public TwitchChatClientManager(
      TwitchClientStateMessagingGateway stateClient,
      ClientFactory<PircBotX,
      TwitchChatClientSettings> clientFactory) {
    this.stateClient = stateClient;
    this.clientFactory = clientFactory;
  }

  @Override
  public void startClient() throws ClientException {

    // Acknowledge the request to start the client
    stateClient.submit(new ClientStartRequestedEvent());

    // Check the clientDelegate hasn't already been generated. If it has, then ignore request.
    if (clientDelegate != null) {
      stateClient
        .submit(new ClientRequestIgnoredEvent("Client already starting/started, ignoring"));
      return;
    }

    // Acknowledge request resulted in client starting
    stateClient.submit(new ClientStartingEvent());

    // Generate a new client delegate, checking it's the type we want
    ClientDelegate<PircBotX, TwitchChatClientSettings> generatedDelegate = clientFactory.generate();
    if (generatedDelegate instanceof TwitchChatClientDelegate) {
      clientDelegate = (TwitchChatClientDelegate) generatedDelegate;
    } else {
      // this should never happen during runtime; this is a developer issue!
      throw new IllegalStateException(
        "Client factory did not return expected delegate. Was: " + generatedDelegate);
    }

    // Acknowledge the client will now start connecting
    stateClient.submit(new ClientConnectingEvent());

    // Start the client.
    // Note the underlying client's startBot() method blocks the calling thread,
    // so we're putting it in its own executor thread!
    executorService.execute(() -> {
      try {
        clientDelegate.getClient().startBot();
      } catch (IOException | IrcException e) {
        log.error("Twitch client encountered an unexpected error during start/run", e);
        stateClient.submit(new ClientErrorEvent(e,
            "Twitch client encountered an unexpected error during start/run: "
                    + e.getMessage()
        ));
      }
    });

  }

  /**
   * Stops and removes underlying PircBotX instance. Note that stopping will not be completed until
   * a corresponding ClientDisconnectedEvent is emitted from the PircBotX listener. Also, if
   * configured, this manager may send a disconnect message to chat so that viewers are aware the
   * bot is out of service.
   */
  @Override
  public void stopClient() {
    privateStop("Stop request by user");
  }

  @Override
  public void reloadClient() throws ClientException {
    stopClient();
    startClient();
  }

  /**
   * Consumes the specified ActionRequest if it can be delegated to PircBotX (sending messages,
   * executing bans, etc.), but only if this manager is in the ${@link ClientReadyEvent} state. If
   * a request comes in during a non-ready state, an ${@link ClientRequestIgnoredEvent} is emitted
   * instead of silently dropping the request entirely.
   */
  @Override
  public void consume(ActionRequest actionRequest) {
    log.debug("Consuming ActionRequest: " + actionRequest);
    if (lastEvent instanceof ClientReadyEvent) {
      try {
        Action action = actionRequest.getAction();
        if (action instanceof SendMessageAction) {
          clientDelegate.sendMessage(((SendMessageAction) action).getMessage());
        }
      } catch (Exception unexpected) {
        stateClient.submit(new ClientErrorEvent(unexpected,
            "Encountered unexpected exception while consuming " + actionRequest));
      }
    } else {
      stateClient.submit(new ClientRequestIgnoredEvent("Ignoring request "
          + actionRequest + ": Client not ready"));
    }

  }

  /**
   * Responds to lifecycle events from the state queue (shared with ${@link PircBotxListener}).
   * Any error event will cause this client manager to shut down. If configured, once in the ready
   * state, this manager can send a join message to chat to let viewers know the bot is available.
   */
  @ServiceActivator(inputChannel = "twitchClientLifecycleChannel")
  @Override
  public void onEvent(ObsChatbotEvent event) {
    lastEvent = event;
    if (event instanceof ClientErrorEvent) {
      // Any error event must stop the client; something is wrong
      privateStop("Stopping due to error: " + ((ClientErrorEvent) event).getMessage());
    } else if (event instanceof ClientDisconnectedEvent) {
      // When disconnected, we know the client is safely disengaged, so
      // we can clear it out and send a Stopped event
      stateClient.submit(new ClientStoppedEvent());
      clientDelegate = null;
    } else if (event instanceof ClientReadyEvent) {
      if (clientDelegate.getSettings().getJoinMessage() != null) {
        consume(new ActionRequest(
            new RequestContext(User.systemUser(), new ArrayList<>()),
            new SendMessageAction(clientDelegate.getSettings().getJoinMessage())
        ));
      }
    }
  }

  private void privateStop(String stopReason) {

    // Acknowledge a stop was requested
    stateClient.submit(new ClientStopRequestedEvent(stopReason));

    // Stop the client if present, else ignore the request
    if (clientDelegate == null) {
      stateClient.submit(new ClientRequestIgnoredEvent("Stop ignored: Client wasn't running"));
      return;
    }

    // Send a closing message if relevant, and wait for it to go through
    if (clientDelegate.getSettings().getLeaveMessage() != null) {
      consume(new ActionRequest(
          new RequestContext(User.systemUser(), new ArrayList<>()),
          new SendMessageAction(clientDelegate.getSettings().getLeaveMessage())
      ));
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    // Acknowledge the request will now result in a stop
    stateClient.submit(new ClientStoppingEvent());

    // Stop the client
    clientDelegate.getClient().stopBotReconnect();

    // Try to close the connection
    try {
      clientDelegate.getClient().close();
    } catch (Exception e) {
      log.warn("Could not close the connection", e);
    }

    // Note the rest of the close process will be completed in the listener!

  }

}
