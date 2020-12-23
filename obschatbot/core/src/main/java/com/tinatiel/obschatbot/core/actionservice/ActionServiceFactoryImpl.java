package com.tinatiel.obschatbot.core.actionservice;

import com.tinatiel.obschatbot.core.action.ActionType;
import com.tinatiel.obschatbot.core.actionservice.obs.ObsClient;
import com.tinatiel.obschatbot.core.actionservice.twitch.TwitchChatClient;

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
