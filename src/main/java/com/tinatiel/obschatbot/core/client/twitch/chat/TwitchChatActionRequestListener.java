package com.tinatiel.obschatbot.core.client.twitch.chat;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.core.messaging.Listener;
import com.tinatiel.obschatbot.core.request.ActionRequest;

public class TwitchChatActionRequestListener implements Listener<ActionRequest> {

    private final ClientManager twitchChatClientManager;

    public TwitchChatActionRequestListener(ClientManager twitchChatClientManager) {
        this.twitchChatClientManager = twitchChatClientManager;
    }

    @Override
    public void onEvent(ActionRequest event) {
        Action action = event.getAction();
        if(action instanceof SendMessageAction) {
            twitchChatClientManager.consume(event);
        }
    }
}
