/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.handler.chat;

import com.tinatiel.obschatbot.core.request.handler.chat.ChatMessageParseResult;
import com.tinatiel.obschatbot.core.request.handler.chat.ChatMessageParser;
import com.tinatiel.obschatbot.core.request.handler.chat.ChatMessageParserImpl;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ChatMessageParserTest {

    @Test
    void messageMissingTriggerReturnsNothing() {

        // Given a parser
        ChatMessageParser parser = new ChatMessageParserImpl("!", true);

        // When parsed with a message not containing the trigger
        Optional<ChatMessageParseResult> result = parser.parse("wont trigger");

        // then nothing returned
        assertThat(result).isNotPresent();

    }

    @Test

    void parseTriggeringOnlyOnFirstChar() {

        // given parser only cares about first char
        ChatMessageParser parser = new ChatMessageParserImpl("!", false);

        // When parsed
        Optional<ChatMessageParseResult> firstCharResult = parser.parse("!run with these args");
        Optional<ChatMessageParseResult> fullMessageResult = parser.parse("@tina should !run with these args");

        // then only the message starting with the trigger returns a result
        ChatMessageParseResult expectedResult = new ChatMessageParseResult("run", Arrays.asList("with", "these", "args"));
        assertThat(firstCharResult).isPresent().get().isEqualToComparingFieldByField(expectedResult);
        assertThat(fullMessageResult).isNotPresent();

    }

    @Test
    void parseTriggeringOnEntireMessage() {

        // given parser parses entire message
        ChatMessageParser parser = new ChatMessageParserImpl("!", true);

        // When parsed
        Optional<ChatMessageParseResult> firstCharResult = parser.parse("!run with these args");
        Optional<ChatMessageParseResult> fullMessageResult = parser.parse("  @tina  should   !run  with   these   args  ");

        // then both messages return a result
        ChatMessageParseResult expectedResult = new ChatMessageParseResult("run", Arrays.asList("with", "these", "args"));
        assertThat(firstCharResult).isPresent().get().isEqualToComparingFieldByField(expectedResult);
        assertThat(fullMessageResult).isPresent().get().isEqualToComparingFieldByField(expectedResult);

    }
}
