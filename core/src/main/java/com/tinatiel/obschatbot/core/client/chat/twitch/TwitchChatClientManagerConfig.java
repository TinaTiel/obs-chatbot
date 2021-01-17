/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.chat.twitch;

import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;
import org.pircbotx.PircBotX;
import org.pircbotx.UtilSSLSocketFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLSocketFactory;

@Configuration
public class TwitchChatClientManagerConfig {

    @Autowired
    ClientSettingsFactory clientSettingsFactory;

    @Bean
    SSLSocketFactory sslSocketFactory() {
        return new UtilSSLSocketFactory();
    }

    @Bean
    ClientFactory<PircBotX> twitchChatClientFactory() {
        return new TwitchChatClientFactory(clientSettingsFactory, sslSocketFactory());
    }

    @Bean
    TwitchChatClientManager twitchChatClientManager() {
        return new TwitchChatClientManagerImpl(twitchChatClientFactory(), clientSettingsFactory);
    }

}