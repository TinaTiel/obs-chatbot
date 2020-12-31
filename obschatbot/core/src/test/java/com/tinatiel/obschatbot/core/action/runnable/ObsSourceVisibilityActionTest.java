/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action.runnable;

import com.tinatiel.obschatbot.core.action.RunnableAction;
import com.tinatiel.obschatbot.core.dispatch.CommandRequestContext;
import com.tinatiel.obschatbot.core.action.ActionType;
import com.tinatiel.obschatbot.core.client.ActionClientFactory;
import com.tinatiel.obschatbot.core.client.obs.ObsClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class ObsSourceVisibilityActionTest {

    CommandRequestContext context;
    ActionClientFactory factory;
    ObsClient client;

    @BeforeEach
    void setUp() {

        // Initialize dependencies and behavior
        context = mock(CommandRequestContext.class);
        factory = mock(ActionClientFactory.class);
        client = mock(ObsClient.class);
        when(factory.getService(ActionType.OBS)).thenReturn(client);

    }

//    @Test
//    void runInvokesObsClient() {
//
//        // Given an action
//        String scene = "some scene";
//        String source = "some source";
//        boolean visible = true;
//        ObsSourceVisibilityRunnableAction action = new ObsSourceVisibilityRunnableAction(context, factory,
//                scene, source, visible);
//
//        // When run
//        action.run();
//
//        // Then the OBS Client is invoked
//        verify(factory).getService(ActionType.OBS);
//        verify(client).setSourceVisibility(scene, source, visible);
//
//    }
//
//    @Test
//    void sourceNameIsRequired() {
//
//        // Given action with no source name
//        RunnableAction action = new ObsSourceVisibilityRunnableAction(context, factory,
//                "foo", null, true);
//
//        // When run, then an exception is thrown
//        assertThatThrownBy(action::run).isInstanceOf(IllegalStateException.class);
//
//    }
//
//    @Test
//    void ContextRequiredToRun() {
//
//        // Given action with no context
//        RunnableAction action = new ObsSourceVisibilityRunnableAction(null, factory,
//                "foo", "bar", true);
//
//        // When run, then an exception is thrown
//        assertThatThrownBy(action::run).isInstanceOf(IllegalStateException.class);
//
//    }
//
//    @Test
//    void factoryRequiredToRun() {
//
//        // Given action with no factory
//        RunnableAction action = new ObsSourceVisibilityRunnableAction(context, null,
//                "foo", "bar", true);
//
//        // When run, then an exception is thrown
//        assertThatThrownBy(action::run).isInstanceOf(IllegalStateException.class);
//
//    }
}
