/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.handler;

import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.request.CommandRequest;
import com.tinatiel.obschatbot.core.request.messaging.CommandRequestGateway;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.request.factory.CommandRequestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommandRequestDispatcherImplTest {

    CommandRequestFactory commandRequestFactory;
    CommandRequestGateway commandRequestGateway;

    CommandRequestDispatcher commandRequestDispatcher;

    @BeforeEach
    void setUp() {
        commandRequestFactory = mock(CommandRequestFactory.class);
        commandRequestGateway = mock(CommandRequestGateway.class);
        commandRequestDispatcher = new CommandRequestDispatcherImpl(commandRequestFactory,
          commandRequestGateway);
    }

    @Test
    void commandAndContextRequired() {
        assertThatThrownBy(() -> {
            commandRequestDispatcher.submit(null, mock(RequestContext.class));
        }).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> {
            commandRequestDispatcher.submit(mock(Command.class), null);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void executorInvokedWithExpectedRequest() {

        // Given the request factory returns a request
        CommandRequest commandRequest = mock(CommandRequest.class);
        when(commandRequestFactory.build(any(), any())).thenReturn(commandRequest);

        // When executed
        commandRequestDispatcher.submit(mock(Command.class), mock(RequestContext.class));

        // Then the underlying executor is invoked with it
        verify(commandRequestGateway).submit(commandRequest);

    }

    @Test
    void whenAnyExceptionsOccurThenExecutorIsNotInvoked() {

        // Given factory throws exception
        when(commandRequestFactory.build(any(), any())).thenThrow(new RuntimeException("some exception, doesn't matter"));

        // When executed
        commandRequestDispatcher.submit(mock(Command.class), mock(RequestContext.class));

        // Then delegated executor is NOT invoked (but we should see logs!)
        verifyNoInteractions(commandRequestGateway);

    }

}