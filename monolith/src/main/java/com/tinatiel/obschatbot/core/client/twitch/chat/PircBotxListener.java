/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.twitch.chat;

import com.tinatiel.obschatbot.core.client.event.ClientAuthenticatedEvent;
import com.tinatiel.obschatbot.core.client.event.ClientAuthenticatingEvent;
import com.tinatiel.obschatbot.core.client.event.ClientConnectedEvent;
import com.tinatiel.obschatbot.core.client.event.ClientDisconnectedEvent;
import com.tinatiel.obschatbot.core.client.event.ClientErrorEvent;
import com.tinatiel.obschatbot.core.client.event.ClientJoinedEvent;
import com.tinatiel.obschatbot.core.client.event.ClientJoiningEvent;
import com.tinatiel.obschatbot.core.client.event.ClientReadyEvent;
import com.tinatiel.obschatbot.core.client.twitch.chat.messaging.TwitchClientLifecycleGateway;
import com.tinatiel.obschatbot.core.request.handler.chat.ChatRequestHandler;
import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.core.user.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectAttemptFailedEvent;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.ExceptionEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.OutputEvent;
import org.pircbotx.hooks.events.SocketConnectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of PircBotX's ${@link ListenerAdapter}, registered with the PircBotX instance
 * during construction. This implementation handles both responding to lifecycle/state events (using
 * a Queue to communicate with ${@link TwitchChatClientManager} during start/stop sequences), and is
 * also responsible for delegating chat messages to the ${@link ChatRequestHandler} in order to
 * trigger ${@link com.tinatiel.obschatbot.core.request.CommandRequest} invocation.
 */
public class PircBotxListener extends ListenerAdapter {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private final TwitchClientLifecycleGateway stateClient;
  private final ChatRequestHandler chatRequestHandler;
  private final TwitchChatClientTagsParser tagsParser;

  /**
   * Construct a new instance of this listener.
   *
   * @param stateClient        submits state events into the queue shared with the manager.
   * @param chatRequestHandler handles chat messages, triggering CommandRequests if applicable.
   * @param tagsParser         parses IRC tags, to help construct the
   *                           ${@link com.tinatiel.obschatbot.core.request.RequestContext}
   *                           required by the ChatRequestHandler.
   */
  public PircBotxListener(
      TwitchClientLifecycleGateway stateClient,
      ChatRequestHandler chatRequestHandler,
      TwitchChatClientTagsParser tagsParser) {
    this.stateClient = stateClient;
    this.chatRequestHandler = chatRequestHandler;
    this.tagsParser = tagsParser;
  }

  /**
   * When we have successfully reached Twitch's server.
   */
  @Override
  public void onSocketConnect(SocketConnectEvent event) throws Exception {
    stateClient.submit(new ClientConnectedEvent());
  }

  /**
   * When we have successfully reached the server, AND successfully authenticated.
   */
  @Override
  public void onConnect(ConnectEvent event) throws Exception {
    stateClient.submit(new ClientAuthenticatedEvent());
  }

  @Override
  public void onJoin(JoinEvent event) throws Exception { // joining the channel

    // Request tags capability so we can determine if a mod, subscriber, etc.
    stateClient.submit(new ClientJoiningEvent("Requesting Tags capability"));
    event.getBot().sendCAP().request("twitch.tv/tags");

    // Request commands capability so we can respond to RECONNECT if issued by Twitch IRC server
    stateClient.submit(new ClientJoiningEvent("Requesting Commands capability"));
    event.getBot().sendCAP().request("twitch.tv/commands");

    stateClient.submit(new ClientJoinedEvent());
    stateClient.submit(new ClientReadyEvent());

  }

  @Override
  public void onNotice(NoticeEvent event) throws Exception {

    if (event.getNotice().toLowerCase().contains("improperly formatted auth")) {
      // "Improperly formatted auth" => improperly formatted auth; developer error
      stateClient.submit(new ClientErrorEvent(
          "Unable to connect to Twitch: Improperly formatted auth, contact the developer!", null
      ));

    } else if (event.getNotice().toLowerCase().contains("unsuccessful")) {
      // "login unsuccessful" => if token doesn't have needed scopes, or
      // isn't an user access token; developer error
      stateClient.submit(new ClientErrorEvent(
          "Unable to connect to Twitch: Not an User Token; contact the developer!", null
      ));

    } else if (
        event.getNotice().toLowerCase().contains("permission to perform")
          || event.getNotice().toLowerCase().contains("authentication failed")
    ) {
      // "You don't have permission to perform that action" => if token
      // revoked during session; user-error
      stateClient.submit(new ClientErrorEvent(
          "Unable to connect to Twitch: Connection (token) has been removed"
            + " or revoked; re-approve and try again.", null
      ));

    }
  }

  @Override
  public void onException(ExceptionEvent event) throws Exception {
    stateClient.submit(
      new ClientErrorEvent("Unexpected error: " + event.getMessage(), event.getException()));
  }

  @Override
  public void onMessage(MessageEvent event) throws Exception {
    log.trace("ON MESSAGE event: " + event);
    log.trace(
        "Received Message from user '"
        + event.getUser().getNick() + "' : '" + event.getMessage() + "'"
    );
    log.trace("Tags: " + event.getTags());
    chatRequestHandler.handle(
        User.builder()
          .platform(Platform.TWITCH)
          .username(event.getUser().getNick())
          .id(tagsParser.getUserIdFromTags(event.getTags()))
          .userSecurityDetails(tagsParser.getDetailsFromTags(event.getTags()))
          .build(),
        event.getMessage()
    );
  }

  @Override
  public void onDisconnect(DisconnectEvent event) throws Exception {
    stateClient.submit(new ClientDisconnectedEvent());
  }

  @Override
  public void onEvent(Event event) throws Exception {
    log.trace("PircBotX: " + event);
    super.onEvent(event);
  }

  @Override
  public void onOutput(OutputEvent event) throws Exception {
    if (event.getRawLine().startsWith("PASS")) {
      stateClient.submit(new ClientAuthenticatingEvent());
    }
    super.onOutput(event);
  }

  @Override
  public void onConnectAttemptFailed(ConnectAttemptFailedEvent event) throws Exception {
    log.error("Failed to connect to Twitch: " + event.getConnectExceptions().entrySet());
    stateClient.submit(new ClientErrorEvent(
        "Could not connect to Twitch, "
        + "your (1) network connection, (2) that Twitch is up, "
        + "and (3) verify the port and host are correct", null
    ));
  }
}
