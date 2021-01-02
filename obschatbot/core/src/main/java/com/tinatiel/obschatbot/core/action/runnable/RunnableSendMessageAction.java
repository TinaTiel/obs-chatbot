/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action.runnable;

import com.tinatiel.obschatbot.core.action.RunnableAction;
import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClient;
import com.tinatiel.obschatbot.core.request.RequestContext;

public class RunnableSendMessageAction implements RunnableAction<TwitchChatClient, SendMessageAction> {

    private final SendMessageAction action;
    private final TwitchChatClient twitchChatClient;
    private final RequestContext context;

    public RunnableSendMessageAction(SendMessageAction action, TwitchChatClient twitchChatClient, RequestContext context) {
        if(action == null || twitchChatClient == null || context == null) throw new IllegalArgumentException("arguments cannot be null");
        this.action = action;
        this.twitchChatClient = twitchChatClient;
        this.context = context;
    }

    @Override
    public RequestContext getRequestContext() {
        return context;
    }

    @Override
    public SendMessageAction getAction() {
        return action;
    }

    @Override
    public TwitchChatClient getClient() {
        return twitchChatClient;
    }

    @Override
    public void run() {
        twitchChatClient.sendMessage(action.getMessage());
    }
}
