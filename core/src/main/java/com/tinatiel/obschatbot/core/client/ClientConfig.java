/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatSettings;
import com.tinatiel.obschatbot.core.client.obs.ObsSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

    @Value("${TWITCH_AUTH:noauth}")
    private String twitchAuth;

    @Bean
    ClientSettingsFactory clientSettingsFactory() {

        return new ClientSettingsFactory() {
            @Override
            public TwitchChatSettings getTwitchChatSettings() {
                return new TwitchChatSettings(
                        "irc.chat.twitch.tv", 6697,
                        "robotiel", twitchAuth, "tinatiel",
                        5000
                );
            }

            @Override
            public ObsSettings getObsSettings() {
                return null;
            }
        };

    }


}
