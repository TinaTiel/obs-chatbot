/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.queue.type;

import com.tinatiel.obschatbot.core.client.ActionClient;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClient;

public class TwitchChatActionQueueType implements ActionQueueType {
    @Override
    public boolean canAccept(Class<? extends ActionClient> recipient) {
        return recipient.equals(TwitchChatClient.class);
    }
}
