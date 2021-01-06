/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.obs;

import com.tinatiel.obschatbot.core.action.model.ObsSourceVisibilityAction;
import com.tinatiel.obschatbot.core.request.queue.ActionCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObsClientManagerImpl implements ObsClientManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @Override
    public void consume(ActionCommand actionCommand) {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void reload() {

    }

    @Override
    public ObsClient getClient() {
        return null;
    }
}
