package com.tinatiel.obschatbot.core.action.impl;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.dispatch.CommandRequest;
import com.tinatiel.obschatbot.core.action.ActionType;
import com.tinatiel.obschatbot.core.actionservice.ActionServiceFactory;
import com.tinatiel.obschatbot.core.actionservice.obs.ObsClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class ObsSourceVisibilityActionTest {

    CommandRequest context;
    ActionServiceFactory factory;
    ObsClient client;

    @BeforeEach
    void setUp() {

        // Initialize dependencies and behavior
        context = mock(CommandRequest.class);
        factory = mock(ActionServiceFactory.class);
        client = mock(ObsClient.class);
        when(factory.getService(ActionType.OBS)).thenReturn(client);

    }

    @Test
    void runInvokesObsClient() {

        // Given an action
        String scene = "some scene";
        String source = "some source";
        boolean visible = true;
        ObsSourceVisibilityAction action = new ObsSourceVisibilityAction(context, factory,
                scene, source, visible);

        // When run
        action.run();

        // Then the OBS Client is invoked
        verify(factory).getService(ActionType.OBS);
        verify(client).setSourceVisibility(scene, source, visible);

    }

    @Test
    void sourceNameIsRequired() {

        // Given action with no source name
        Action action = new ObsSourceVisibilityAction(context, factory,
                "foo", null, true);

        // When run, then an exception is thrown
        assertThatThrownBy(action::run).isInstanceOf(IllegalStateException.class);

    }

    @Test
    void ContextRequiredToRun() {

        // Given action with no context
        Action action = new ObsSourceVisibilityAction(null, factory,
                "foo", "bar", true);

        // When run, then an exception is thrown
        assertThatThrownBy(action::run).isInstanceOf(IllegalStateException.class);

    }

    @Test
    void factoryRequiredToRun() {

        // Given action with no factory
        Action action = new ObsSourceVisibilityAction(context, null,
                "foo", "bar", true);

        // When run, then an exception is thrown
        assertThatThrownBy(action::run).isInstanceOf(IllegalStateException.class);

    }
}
