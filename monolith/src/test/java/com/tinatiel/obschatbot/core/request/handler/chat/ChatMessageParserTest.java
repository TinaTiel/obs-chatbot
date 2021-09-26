/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.handler.chat;

import com.tinatiel.obschatbot.data.client.twitch.chat.TwitchClientChatDataService;
import com.tinatiel.obschatbot.data.client.twitch.chat.model.TwitchClientChatDataDto;
import com.tinatiel.obschatbot.security.owner.OwnerDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChatMessageParserTest {

    OwnerService ownerService;
    TwitchClientChatDataService dataService;

    ChatMessageParser parser;

    @BeforeEach
    void setUp() {
        ownerService = mock(OwnerService.class);
        when(ownerService.getOwner()).thenReturn(mock(OwnerDto.class));
        dataService = mock(TwitchClientChatDataService.class);
        parser = new ChatMessageParserImpl(ownerService, dataService);
    }

    @Test
    void messageMissingTriggerReturnsNothing() {

        // Given the entire message is parsed for a trigger
//        ChatMessageParser parser = new ChatMessageParserImpl("!", true);
        when(dataService.findByOwner(any())).thenReturn(Optional.of(TwitchClientChatDataDto.builder()
          .trigger("!")
          .parseEntireMessage(true)
          .build()));

        // When parsed with a message not containing the trigger
        Optional<ChatMessageParseResult> result = parser.parse("wont trigger");

        // then nothing returned
        assertThat(result).isNotPresent();

    }

    @Test

    void parseTriggeringOnlyOnFirstChar() {

        // given parser only cares about first char
//        ChatMessageParser parser = new ChatMessageParserImpl("!", false);
        when(dataService.findByOwner(any())).thenReturn(Optional.of(TwitchClientChatDataDto.builder()
          .trigger("!")
          .parseEntireMessage(false)
          .build()));

        // When parsed
        Optional<ChatMessageParseResult> firstCharResult = parser.parse("!run with these args");
        Optional<ChatMessageParseResult> fullMessageResult = parser.parse("@tina should !run with these args");

        // then only the message starting with the trigger returns a result
        ChatMessageParseResult expectedResult = new ChatMessageParseResult("run", Arrays.asList("with", "these", "args"));
        assertThat(firstCharResult).isPresent().get().usingRecursiveComparison().isEqualTo(expectedResult);
        assertThat(fullMessageResult).isNotPresent();

    }

    @Test
    void parseTriggeringOnEntireMessage() {

        // given parser parses entire message
//        ChatMessageParser parser = new ChatMessageParserImpl("!", true);
        when(dataService.findByOwner(any())).thenReturn(Optional.of(TwitchClientChatDataDto.builder()
          .trigger("!")
          .parseEntireMessage(true)
          .build()));

        // When parsed
        Optional<ChatMessageParseResult> firstCharResult = parser.parse("!run with these args");
        Optional<ChatMessageParseResult> fullMessageResult = parser.parse("  @tina  should   !run  with   these   args  ");

        // then both messages return a result
        ChatMessageParseResult expectedResult = new ChatMessageParseResult("run", Arrays.asList("with", "these", "args"));
        assertThat(firstCharResult).isPresent().get().isEqualToComparingFieldByField(expectedResult);
        assertThat(fullMessageResult).isPresent().get().isEqualToComparingFieldByField(expectedResult);

    }
}
