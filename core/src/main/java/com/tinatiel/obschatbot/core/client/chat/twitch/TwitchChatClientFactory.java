/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.chat.twitch;

import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;
import org.pircbotx.PircBotX;

import javax.net.ssl.SSLSocketFactory;
import java.util.concurrent.CompletableFuture;

public class TwitchChatClientFactory implements ClientFactory<PircBotX> {

    private final ClientSettingsFactory clientSettingsFactory;
    private final SSLSocketFactory sslSocketFactory;

    public TwitchChatClientFactory(ClientSettingsFactory clientSettingsFactory, SSLSocketFactory sslSocketFactory) {
        this.clientSettingsFactory = clientSettingsFactory;
        this.sslSocketFactory = sslSocketFactory;
    }

    @Override
    public PircBotX generate(CompletableFuture<Void> ready, CompletableFuture<Void> disconnected) {
        TwitchChatSettings settings = clientSettingsFactory.getTwitchChatSettings();
        PircBotX bot = new PircBotX(new org.pircbotx.Configuration.Builder()
                .addServer(settings.getHost(), settings.getPort()) // Twitch's IRC url
                .setSocketFactory(sslSocketFactory)
                .addAutoJoinChannel("#" + settings.getBroadcasterChannel()) // channel is same as streamer's username
                //                 .addAutoJoinChannel("asdlfjasldheowpoasiefjl")
                .setName(settings.getUsername())             // account we're connecting as
                .setServerPassword(settings.getPassword())   // generated with TMI for now
                //                 .setServerPassword("wrong")
                .addListener(new PircBotxListener(ready, disconnected))   // have to register the listener!
                .setOnJoinWhoEnabled(false) // Twitch does not support WHO
                .buildConfiguration()
        );

        return bot;
    }

}
