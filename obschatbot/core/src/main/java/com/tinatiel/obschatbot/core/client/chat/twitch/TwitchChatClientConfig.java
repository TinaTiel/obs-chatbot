/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.chat.twitch;

import org.pircbotx.MultiBotManager;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.hooks.Listener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.net.ssl.SSLSocketFactory;

@Configuration
public class TwitchChatClientConfig {

    @Value("${TWITCH_AUTH}")
    private String twitchAuth;

    org.pircbotx.Configuration botConfiguration() {
        org.pircbotx.Configuration config = new org.pircbotx.Configuration.Builder()
                .addServer("irc.chat.twitch.tv", 6697) // Twitch's IRC url
                .setSocketFactory(sslSocketFactory())
                .addAutoJoinChannel("#tinatiel") // channel is same as streamer's username
                .setName("robotiel")             // account we're connecting as
                .setServerPassword(twitchAuth)   // generated with TMI for now
                .addListener(myListener())   // have to register the listener!
                .buildConfiguration();

        return config;
    }

    @Bean
    SSLSocketFactory sslSocketFactory() {
        return new UtilSSLSocketFactory();
    }

    @Bean
    Listener myListener() { return new MyListener(fooService()); }

    @Bean
    FooService fooService() { return new FooServiceImpl(); }

    @Lazy
    @Bean
    MultiBotManager multiBotManager() {
        MultiBotManager manager = new MultiBotManager();
        manager.addBot(botConfiguration());
        return manager;
    }

    @Lazy
    @Bean
    TwitchChatClient twitchChatClient() {
        return new TwitchChatClientImpl(multiBotManager());
    }

}
