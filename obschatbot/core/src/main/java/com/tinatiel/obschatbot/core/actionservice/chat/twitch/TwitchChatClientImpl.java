/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.actionservice.chat.twitch;

import org.pircbotx.MultiBotManager;
import org.pircbotx.PircBotX;

public class TwitchChatClientImpl implements TwitchChatClient {

    private final PircBotX pircBotX;

    public TwitchChatClientImpl(MultiBotManager manager) {
        pircBotX = manager.getBots().first();
    }

    @Override
    public void sendMessage(String message) {
        System.out.println("Sending your message: " + message);
        pircBotX.sendIRC().message("#tinatiel", message);
    }
}
