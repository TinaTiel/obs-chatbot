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

    @Value("${TWITCH_TARGET_CHANNEL:noauth}")
    private String targetChannel;

    @Value("${TWITCH_USER:noauth}")
    private String twitchUsername;

    @Value("${TWITCH_PASS:noauth}")
    private String twitchPassword;

    @Bean
    ClientSettingsFactory clientSettingsFactory() {

        return new ClientSettingsFactory() {
            @Override
            public TwitchChatSettings getTwitchChatSettings() {
                return new TwitchChatSettings(
                        TwitchChatSettings.DEFAULT_HOST, TwitchChatSettings.DEFAULT_PORT,
                        twitchUsername, twitchPassword, targetChannel,
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
