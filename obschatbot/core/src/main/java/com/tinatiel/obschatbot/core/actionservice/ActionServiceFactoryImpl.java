/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.actionservice;

import com.tinatiel.obschatbot.core.action.ActionType;
import com.tinatiel.obschatbot.core.actionservice.obs.ObsClient;
import com.tinatiel.obschatbot.core.actionservice.chat.twitch.TwitchChatClient;

public class ActionServiceFactoryImpl implements ActionServiceFactory {

    private final ObsClient obsClient;
    private final TwitchChatClient twitchChatClient;

    public ActionServiceFactoryImpl(ObsClient obsClient, TwitchChatClient twitchChatClient) {
        this.obsClient = obsClient;
        this.twitchChatClient = twitchChatClient;
    }

    @Override
    public ActionService getService(ActionType actionType) {
        if(actionType == null) {
            throw new IllegalArgumentException("ActionType cannot be null");
        }

        switch (actionType) {
            case OBS:
                return obsClient;

            case TWITCH_CHAT:
                return twitchChatClient;

            default:
                throw new UnsupportedOperationException("No service defined for actionType: " + actionType);
        }
    }
}
