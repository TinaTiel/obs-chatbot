package com.tinatiel.obschatbot.core.client.chat.twitch;

import com.tinatiel.obschatbot.core.client.ClientDelegate;
import org.pircbotx.PircBotX;

public class TwitchChatClientDelegate implements ClientDelegate<PircBotX, TwitchChatClientSettings> {

    private final PircBotX delegate;
    private final TwitchChatClientSettings settings;

    public TwitchChatClientDelegate(PircBotX delegate, TwitchChatClientSettings settings) {
        this.delegate = delegate;
        this.settings = settings;
    }

    @Override
    public PircBotX getClient() {
        return delegate;
    }

    @Override
    public TwitchChatClientSettings getSettings() {
        return settings;
    }

    public void sendMessage(String message) {
        delegate.sendIRC().message(
                "#" + settings.getBroadcasterChannel(),
                message
        );
    }
}
