/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.chat.twitch;

import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.core.client.Listener;
import com.tinatiel.obschatbot.core.client.StateMessage;
import org.pircbotx.PircBotX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwitchListener implements Listener {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onState(ClientManager clientManager, StateMessage stateMessage) {
        switch(stateMessage.getState()) {
            case ERROR:
                clientManager.stopClient();
                break;
            default:
                log.debug("onState: " + stateMessage);
        }
    }
}
