/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.twitch.chat;

import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;

public class TwitchChatClientSettingsFactory implements ClientSettingsFactory<TwitchChatClientSettings> {

    private final TwitchChatClientSettings settings;

    public TwitchChatClientSettingsFactory(TwitchChatClientSettings settings) {
        this.settings = settings;
    }

    @Override
    public TwitchChatClientSettings getSettings() {
        return settings;
    }
}
