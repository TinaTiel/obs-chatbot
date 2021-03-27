/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.remove.dispatch;

import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.messaging.QueueClient;
import com.tinatiel.obschatbot.core.request.CommandRequest;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.request.factory.RequestFactory;
import com.tinatiel.obschatbot.core.request.handler.CommandRequestDispatcher;
import com.tinatiel.obschatbot.core.request.handler.CommandRequestDispatcherImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommandRequestDispatcherImplTest {

    RequestFactory requestFactory;
    QueueClient<CommandRequest> commandRequestQueueClient;

    CommandRequestDispatcher commandRequestDispatcher;

    @BeforeEach
    void setUp() {
        requestFactory = mock(RequestFactory.class);
        commandRequestQueueClient = mock(QueueClient.class);
        commandRequestDispatcher = new CommandRequestDispatcherImpl(requestFactory, commandRequestQueueClient);
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
        when(requestFactory.build(any(), any())).thenReturn(commandRequest);

        // When executed
        commandRequestDispatcher.submit(mock(Command.class), mock(RequestContext.class));

        // Then the underlying executor is invoked with it
        verify(commandRequestQueueClient).submit(commandRequest);

    }

    @Test
    void whenAnyExceptionsOccurThenExecutorIsNotInvoked() {

        // Given factory throws exception
        when(requestFactory.build(any(), any())).thenThrow(new RuntimeException("some exception, doesn't matter"));

        // When executed
        commandRequestDispatcher.submit(mock(Command.class), mock(RequestContext.class));

        // Then delegated executor is NOT invoked (but we should see logs!)
        verifyNoInteractions(commandRequestQueueClient);

    }

}