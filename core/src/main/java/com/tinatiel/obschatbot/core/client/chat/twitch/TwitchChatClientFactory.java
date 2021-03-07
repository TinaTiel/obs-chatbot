/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.chat.twitch;

import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;
import com.tinatiel.obschatbot.core.client.Listener;
import org.pircbotx.PircBotX;

import javax.net.ssl.SSLSocketFactory;

public class TwitchChatClientFactory implements ClientFactory<PircBotX> {

    private final ClientSettingsFactory clientSettingsFactory;
    private final SSLSocketFactory sslSocketFactory;
    private final ClientManager<PircBotX> clientManager;
    private final Listener listener;

    public TwitchChatClientFactory(ClientSettingsFactory clientSettingsFactory,
                                   SSLSocketFactory sslSocketFactory,
                                   ClientManager<PircBotX> clientManager,
                                   Listener listener) {
        this.clientSettingsFactory = clientSettingsFactory;
        this.sslSocketFactory = sslSocketFactory;
        this.clientManager = clientManager;
        this.listener = listener;
    }

    @Override
    public PircBotX generate() {
        TwitchChatSettings settings = clientSettingsFactory.getTwitchChatSettings();
        PircBotX bot = new PircBotX(new org.pircbotx.Configuration.Builder()
                .addServer(settings.getHost(), settings.getPort()) // Twitch's IRC url
                .setSocketFactory(sslSocketFactory)
                .addAutoJoinChannel("#" + settings.getBroadcasterChannel()) // channel is same as streamer's username
                .setName(settings.getUsername())             // account we're connecting as
                .setServerPassword(settings.getPassword())   // generated with TMI for now
                .addListener(new PircBotxListener(listener, clientManager))   // have to register the listener!
                .setOnJoinWhoEnabled(false) // Twitch does not support WHO
                .buildConfiguration()
        );

        return bot;
    }

}
