/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.handler.chat;

import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.data.command.CommandService;
import com.tinatiel.obschatbot.core.request.handler.CommandRequestDispatcher;
import com.tinatiel.obschatbot.core.user.User;
import com.tinatiel.obschatbot.core.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ChatCommandRequestHandlerTest {

    User user;

    ChatMessageParser parser;
    CommandService commandService;
    UserService userService;
    CommandRequestDispatcher dispatcher;

    ChatRequestHandler handler;

    @BeforeEach
    void setUp() {
        user = mock(User.class);

        parser = mock(ChatMessageParser.class);
        commandService = mock(CommandService.class);
        userService = mock(UserService.class);
        dispatcher = mock(CommandRequestDispatcher.class);

        handler = new ChatRequestHandlerImpl(parser, commandService, userService, dispatcher);

    }

    @Test
    void regularMessagesAreIgnored() {

        // Given a chat message containing no trigger
        String message = "some message";
        when(parser.parse(any())).thenReturn(Optional.empty());

        // When handled
        handler.handle(user, message);

        // Then parser is invoked
        verify(parser).parse(message);

        // But the dispatch isn't notified
        verifyNoInteractions(dispatcher);

    }

    @Test
    void unknownCommand() {

        // Given a chat message parsed into results
        ChatMessageParseResult parseResult = new ChatMessageParseResult("someCommand", Arrays.asList("for", "my", "friend"));
        when(parser.parse(any())).thenReturn(Optional.of(parseResult));

        // But no command is found
        when(commandService.findByName(any())).thenReturn(Optional.empty());

        // When handled
        handler.handle(user, "doesn't matter");

        // Then dispatch isn't notified
        verifyNoInteractions(dispatcher);

    }

    @Test
    void messageContainingCommandInvokesDispatcher() {

        // Given a chat message parsed into results
        ChatMessageParseResult parseResult = new ChatMessageParseResult("someCommand", Arrays.asList("for", "my", "friend"));
        when(parser.parse(any())).thenReturn(Optional.of(parseResult));

        // And a command is found
        Command command = mock(Command.class);
        when(commandService.findByName(any())).thenReturn(Optional.of(command));

        // And the full user is retrieved
        when(userService.getUserFromPartial(any())).thenReturn(user);

        // When handled
        handler.handle(user, "doesn't matter");

        // Then dispatcher is invoked
        verify(dispatcher).submit(eq(command), any());

    }
}
