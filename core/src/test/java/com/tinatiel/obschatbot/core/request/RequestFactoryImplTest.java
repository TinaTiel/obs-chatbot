/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.client.ActionClientFactory;
import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.error.CyclicalActionsException;
import com.tinatiel.obschatbot.core.request.dispatch.CommandExecutorService;
import com.tinatiel.obschatbot.core.request.dispatch.SequentialExecutorImpl;
import com.tinatiel.obschatbot.core.request.expand.CommandExpander;
import com.tinatiel.obschatbot.core.request.queue.MainQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RequestFactoryImplTest {

    CommandExpander commandExpander;
    ActionClientFactory clientFactory;
    CommandExecutorService commandExecutorService;
    MainQueue mainQueue;

    RequestFactory factory;

    @BeforeEach
    void setUp() {
        commandExpander = mock(CommandExpander.class);
        clientFactory = mock(ActionClientFactory.class);
        commandExecutorService = mock(CommandExecutorService.class);
        mainQueue = mock(MainQueue.class);
        factory = new RequestFactoryImpl(commandExpander,69L, mainQueue);
    }

    @Test
    void generateExpectedRequest() {

        // Given a command and actions
        Command command = mock(Command.class);
        List<Action> actions = Arrays.asList(
                mock(Action.class),
                mock(Action.class),
                mock(Action.class)
        );

        // Given the expander generates the expected actions
        when(commandExpander.expand(command)).thenReturn(actions);

        // And given a request context
        RequestContext context = mock(RequestContext.class);

        // When the request is generated
        Request request = factory.build(command, context);

        // Then the request contains the expected
//        assertThat(request.getExecutor()).isNotNull().isInstanceOf(SequentialExecutorImpl.class);
        assertThat(request.getActionCommands()).hasSameSizeAs(actions);

    }

    @Test
    void commandAndContextRequired() {
        assertThatThrownBy(() -> {
            factory.build(null, mock(RequestContext.class));
        }).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> {
            factory.build(mock(Command.class), null);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void whenCyclicalActionsThenRethrow() {

        // Given expansion causes a cyclical action exception
        when(commandExpander.expand(any())).thenThrow(new CyclicalActionsException("foo", null));

        // Then when called, rethrow it
        assertThatThrownBy(() -> {
            factory.build(mock(Command.class), mock(RequestContext.class));
        }).isInstanceOf(CyclicalActionsException.class);

    }
}