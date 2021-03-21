/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.chat.twitch;

import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;

public class TwitchChatClientSettingsFactory implements ClientSettingsFactory<TwitchChatSettings> {

    private final TwitchChatSettings settings;

    public TwitchChatClientSettingsFactory(TwitchChatSettings settings) {
        this.settings = settings;
    }

    @Override
    public TwitchChatSettings getSettings() {
        return settings;
    }
}
