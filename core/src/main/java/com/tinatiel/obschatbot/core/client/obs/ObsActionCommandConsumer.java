/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.obs;

import com.tinatiel.obschatbot.core.action.model.ObsSourceVisibilityAction;
import com.tinatiel.obschatbot.core.request.queue.ActionCommand;
import com.tinatiel.obschatbot.core.request.queue.consumers.ActionCommandConsumer;
import net.twasi.obsremotejava.OBSRemoteController;

public class ObsActionCommandConsumer implements ActionCommandConsumer {

    private final OBSRemoteController client;

    public ObsActionCommandConsumer(OBSRemoteController client) {
        this.client = client;
    }

    @Override
    public void consume(ActionCommand actionCommand) {
        if(actionCommand.getAction() instanceof ObsSourceVisibilityAction) {
            ObsSourceVisibilityAction action = (ObsSourceVisibilityAction) actionCommand.getAction();
            client.setSourceVisibility(action.getSceneName(), action.getSourceName(), action.isVisible(), (result) -> {
                actionCommand.complete(null);
            });
        }
    }
}
