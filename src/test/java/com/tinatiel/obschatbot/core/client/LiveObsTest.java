/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

import com.tinatiel.obschatbot.App;
import com.tinatiel.obschatbot.core.action.model.ObsSourceVisibilityAction;
import com.tinatiel.obschatbot.core.client.obs.ObsClient;
import com.tinatiel.obschatbot.core.client.obs.ObsClientManager;
import com.tinatiel.obschatbot.core.error.ClientException;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.core.user.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.fail;

@Disabled
@Import({App.class})
@ExtendWith(SpringExtension.class)
public class LiveObsTest {

    @Autowired ObsClientManager obsClientManager;

    @Test
    void exerciseClientManagerSmokeTest() {

        // Given an actionCommand
        ObsSourceVisibilityAction action = new ObsSourceVisibilityAction(null, "Image", false);
        User user = new User(Platform.TWITCH, "mango");
        RequestContext requestContext = new RequestContext(user, new ArrayList<>());
        ActionRequest actionRequest = new ActionRequest(action, requestContext);

        // Then we can start/stop/reload the manager and consume messages without exceptions
        try {
            obsClientManager.startClient();
            obsClientManager.consume(actionRequest);
            obsClientManager.reloadClient();
            obsClientManager.consume(actionRequest);
            obsClientManager.stopClient();
        } catch (ClientException e) {
            fail("Test requires instance of OBS", e);
        } catch (Exception unexpected) {
            fail("Exercising the clientManager should not throw unexpected exceptions", unexpected);
        }

    }

}
