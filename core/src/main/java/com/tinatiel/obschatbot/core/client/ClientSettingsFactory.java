/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatSettings;
import com.tinatiel.obschatbot.core.client.obs.ObsSettings;

/**
 * Returns immutable copies of settings from their source; e.g. would have dependencies on the database, but won't return mutable proxies.
 */
public interface ClientSettingsFactory {

    TwitchChatSettings getTwitchChatSettings();
    ObsSettings getObsSettings();

}
