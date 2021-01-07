/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

import com.tinatiel.obschatbot.core.client.obs.ObsClient;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClient;
import com.tinatiel.obschatbot.core.error.ClientNotAvailableException;

import java.util.HashMap;
import java.util.Map;

public class ActionClientFactoryImpl implements ActionClientFactory {

    private final ObsClient obsClient;
    private final TwitchChatClient twitchChatClient;

    private final Map<Class<? extends ActionClient>, ActionClient> registry = new HashMap<>();

    public ActionClientFactoryImpl(ObsClient obsClient, TwitchChatClient twitchChatClient) {
        this.obsClient = obsClient;
        this.twitchChatClient = twitchChatClient;

        registry.put(ObsClient.class, obsClient);
        registry.put(TwitchChatClient.class, twitchChatClient);

    }

//    @Override
//    public ActionClient getService(ActionType actionType) {
//        if(actionType == null) {
//            throw new IllegalArgumentException("ActionType cannot be null");
//        }
//
//        switch (actionType) {
//            case OBS:
//                return obsClient;
//
//            case TWITCH_CHAT:
//                return twitchChatClient;
//
//            default:
//                throw new UnsupportedOperationException("No service defined for actionType: " + actionType);
//        }
//    }

    @Override
    public ActionClient getClient(Class<? extends ActionClient> clientType)  throws ClientNotAvailableException {
        if(clientType == null) throw new IllegalArgumentException("clientType cannot be null");
        ActionClient found = registry.get(clientType);
        if(found == null) throw new ClientNotAvailableException("No client registered of type: " + clientType.getSimpleName());
        return found;
    }

}
