/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.dispatch;

import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.request.Request;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.request.RequestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommandDispatcherImplTest {

    RequestFactory requestFactory;
    CommandExecutorService executorService;

    CommandDispatcher commandDispatcher;

    @BeforeEach
    void setUp() {
        requestFactory = mock(RequestFactory.class);
        executorService = mock(CommandExecutorService.class);
        commandDispatcher = new CommandDispatcherImpl(requestFactory, executorService);
    }

    @Test
    void commandAndContextRequired() {
        assertThatThrownBy(() -> {
            commandDispatcher.submit(null, mock(RequestContext.class));
        }).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> {
            commandDispatcher.submit(mock(Command.class), null);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void executorInvokedWithExpectedRequest() {

        // Given the request factory returns a request
        Request request = mock(Request.class);
        when(requestFactory.build(any(), any())).thenReturn(request);

        // When executed
        commandDispatcher.submit(mock(Command.class), mock(RequestContext.class));

        // Then the underlying executor is invoked with it
        verify(executorService).submit(request);

    }

    @Test
    void whenAnyExceptionsOccurThenExecutorIsNotInvoked() {

        // Given factory throws exception
        when(requestFactory.build(any(), any())).thenThrow(new RuntimeException("some exception, doesn't matter"));

        // When executed
        commandDispatcher.submit(mock(Command.class), mock(RequestContext.class));

        // Then delegated executor is NOT invoked (but we should see logs!)
        verifyNoInteractions(executorService);

    }

}