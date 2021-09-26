/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.handler.chat;

import com.tinatiel.obschatbot.data.client.twitch.chat.TwitchClientChatDataService;
import com.tinatiel.obschatbot.data.client.twitch.chat.model.TwitchClientChatDataDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * Parse a chat message for a command prefixed with the specified trigger.
 */
public class ChatMessageParserImpl implements ChatMessageParser {

//  private final String trigger;
//  private final boolean parseEntireMessage;

//  /**
//   * Specify how a chat message will be parsed.
//   *
//   * @param trigger            the trigger word (or character) that prefixes the command to execute
//   * @param parseEntireMessage if true, will look through the entire message for the trigger.
//   *                           Otherwise expects the first word in the message to be the trigger.
//   */
//  public ChatMessageParserImpl(String trigger, boolean parseEntireMessage) {
//    this.trigger = trigger;
//    this.parseEntireMessage = parseEntireMessage;
//  }

  private final OwnerService ownerService;
  private final TwitchClientChatDataService dataService;

  public ChatMessageParserImpl(OwnerService ownerService,
    TwitchClientChatDataService dataService) {
    this.ownerService = ownerService;
    this.dataService = dataService;
  }

  @Override
  public Optional<ChatMessageParseResult> parse(String message) {

    TwitchClientChatDataDto settings = dataService.findByOwner(ownerService.getOwner().getId())
      .orElseThrow(() -> new IllegalStateException("Could not retrieve chat settings"));

    String trigger = settings.getTrigger();
    if (settings.isParseEntireMessage()) {
      if (message.contains(trigger)) {
        return Optional.of(parseMessage(message, trigger));
      }
    } else {
      if (message.startsWith(trigger)) {
        return Optional.of(parseMessage(message, trigger));
      }
    }
    return Optional.empty();
  }

  private ChatMessageParseResult parseMessage(String message, String trigger) {
    String afterTrigger = StringUtils.substringAfter(message, trigger).trim();
    List<String> parts = Arrays.stream(StringUtils.split(afterTrigger))
        .collect(Collectors.toList());

    return new ChatMessageParseResult(parts.get(0),
      parts.size() > 1
        ? parts.subList(1, parts.size())
        : new ArrayList<>());
  }

}
