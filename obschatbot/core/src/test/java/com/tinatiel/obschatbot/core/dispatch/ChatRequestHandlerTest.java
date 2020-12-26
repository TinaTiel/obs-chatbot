package com.tinatiel.obschatbot.core.dispatch;

import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.command.CommandRepository;
import com.tinatiel.obschatbot.core.dispatch.chat.*;
import com.tinatiel.obschatbot.core.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ChatRequestHandlerTest {

    User user;

    ChatMessageParser parser;
    CommandRepository commandRepository;
    CommandDispatcher dispatcher;

    ChatRequestHandler handler;

    @BeforeEach
    void setUp() {
        user = mock(User.class);

        parser = mock(ChatMessageParser.class);
        commandRepository = mock(CommandRepository.class);
        dispatcher = mock(CommandDispatcher.class);

        handler = new ChatRequestHandlerImpl(parser, commandRepository, dispatcher);

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
        when(commandRepository.findByName(any())).thenReturn(Optional.empty());

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
        when(commandRepository.findByName(any())).thenReturn(Optional.of(command));

        // When handled
        handler.handle(user, "doesn't matter");

        // Then dispatcher is invoked
        verify(dispatcher).submit(any());

    }
}
