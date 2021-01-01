/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action.runnable;

import com.tinatiel.obschatbot.core.action.RunnableAction;
import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClient;
import com.tinatiel.obschatbot.core.dispatch.CommandRequestContext;

public class RunnableSendMessageAction implements RunnableAction<TwitchChatClient, SendMessageAction> {

    private final SendMessageAction action;
    private final TwitchChatClient twitchChatClient;
    private final CommandRequestContext context;

    public RunnableSendMessageAction(SendMessageAction action, TwitchChatClient twitchChatClient, CommandRequestContext context) {
        this.action = action;
        this.twitchChatClient = twitchChatClient;
        this.context = context;
    }

    @Override
    public CommandRequestContext getRequestContext() {
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

    }
}
