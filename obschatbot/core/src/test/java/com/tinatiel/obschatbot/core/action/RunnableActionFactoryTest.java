/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action;

import com.tinatiel.obschatbot.core.client.ActionClientFactory;
import com.tinatiel.obschatbot.core.dispatch.CommandRequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RunnableActionFactoryTest {

    ActionClientFactory actionClientFactory;

    RunnableActionFactory runnableActionFactory;

    @BeforeEach
    void setUp() {
        actionClientFactory = mock(ActionClientFactory.class);
        runnableActionFactory = new RunnableActionFactoryImpl(actionClientFactory);
    }

    @Test
    void whenClientNotFoundThenThrowException() {

        // Given ActionClientFactory doesn't find a client
        when(actionClientFactory.getService(any())).thenReturn(null);

        // When called, then an exception is thrown
        assertThatThrownBy(() ->  {
            runnableActionFactory.createForContext(mock(Action.class), mock(CommandRequestContext.class));
        }).isInstanceOf(IllegalStateException.class);

    }

    @Test
    void nullArguments() {
        assertThatThrownBy(() ->  {
            runnableActionFactory.createForContext(null, mock(CommandRequestContext.class));
        }).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() ->  {
            runnableActionFactory.createForContext(mock(Action.class), null);
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
