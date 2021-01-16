/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.chat.twitch;

import org.pircbotx.UtilSSLSocketFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLSocketFactory;

@Configuration
public class TwitchChatClientManagerConfig {

    @Value("${TWITCH_AUTH:noauth}")
    private String twitchAuth;

    @Bean
    SSLSocketFactory sslSocketFactory() {
        return new UtilSSLSocketFactory();
    }

    @Bean
    TwitchChatSettings twitchChatSettings() {
        return new TwitchChatSettings(
                "robotiel", twitchAuth, "tinatiel", 5000
        );
    }

    @Bean
    TwitchChatClientManager twitchChatClientManager() {
        return new TwitchChatClientManagerImpl(twitchChatSettings(), sslSocketFactory());
    }

}
