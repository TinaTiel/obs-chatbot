/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action.runnable;

import com.tinatiel.obschatbot.core.action.model.ObsSourceVisibilityAction;
import com.tinatiel.obschatbot.core.request.ObsChatbotRequestContext;
import com.tinatiel.obschatbot.core.client.obs.ObsClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class ObsSourceVisibilityActionTest {

    ObsChatbotRequestContext context;
    ObsClient client;

    @BeforeEach
    void setUp() {

        // Initialize dependencies and behavior
        context = mock(ObsChatbotRequestContext.class);
        client = mock(ObsClient.class);

    }

    @Test
    void runInvokesObsClient() {

        // Given an action
        String scene = "some scene";
        String source = "some source";
        boolean visible = true;
        ObsSourceVisibilityAction action = new ObsSourceVisibilityAction(scene, source, visible);
        RunnableObsSourceVisibilityAction runnableAction = new RunnableObsSourceVisibilityAction(
                action,
                client,
                context
        );

        // When run
        runnableAction.run();

        // Then the OBS Client is invoked
        verify(client).setSourceVisibility(scene, source, visible);

    }

    @Test
    void sourceNameIsRequired() {

        // Given action with no source name
        ObsSourceVisibilityAction action = new ObsSourceVisibilityAction("some scene", null, true);
        RunnableObsSourceVisibilityAction runnableAction = new RunnableObsSourceVisibilityAction(
                action,
                client,
                context
        );

        // When run, then an exception is thrown
        assertThatThrownBy(runnableAction::run).isInstanceOf(IllegalStateException.class);

    }

    @Test
    void ContextRequiredToRun() {

        // Given action with no context
        ObsSourceVisibilityAction action = new ObsSourceVisibilityAction("foo", "bar", true);
        RunnableObsSourceVisibilityAction runnableAction = new RunnableObsSourceVisibilityAction(
                action,
                client,
                null
        );

        // When run, then an exception is thrown
        assertThatThrownBy(runnableAction::run).isInstanceOf(IllegalStateException.class);

    }

}
