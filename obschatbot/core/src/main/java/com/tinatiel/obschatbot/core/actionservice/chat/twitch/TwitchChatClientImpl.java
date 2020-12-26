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
