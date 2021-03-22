/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.chat.twitch;

import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;
import org.pircbotx.PircBotX;

import javax.net.ssl.SSLSocketFactory;

public class TwitchChatClientFactory implements ClientFactory<PircBotX> {

    private final ClientSettingsFactory<TwitchChatClientSettings> clientSettingsFactory;
    private final SSLSocketFactory sslSocketFactory;
    private final PircBotxListener pircBotxListener;

    public TwitchChatClientFactory(ClientSettingsFactory clientSettingsFactory,
                                   SSLSocketFactory sslSocketFactory,
                                   PircBotxListener pircBotxListener) {
        this.clientSettingsFactory = clientSettingsFactory;
        this.sslSocketFactory = sslSocketFactory;
        this.pircBotxListener = pircBotxListener;
    }

    @Override
    public PircBotX generate() {

        // Get a fresh set of settings
        TwitchChatClientSettings settings = clientSettingsFactory.getSettings();

        // Create a new bot with those settings
        PircBotX bot = new PircBotX(new org.pircbotx.Configuration.Builder()
                .addServer(settings.getHost(), settings.getPort()) // Twitch's IRC url
                .setSocketFactory(sslSocketFactory)
                .addAutoJoinChannel("#" + settings.getBroadcasterChannel()) // channel is same as streamer's username
                .setName(settings.getUsername())             // account we're connecting as
                .setServerPassword(settings.getPassword())   // generated with TMI for now
                .addListener(pircBotxListener)   // have to register the listener!
                .setOnJoinWhoEnabled(false) // Twitch does not support WHO
                .setAutoReconnectAttempts(settings.getConnectionAttempts())
                .setSocketConnectTimeout(Long.valueOf(settings.getConnectionTimeoutMs()).intValue()) // millis timeout, weirdly supplied as int
                .buildConfiguration()
        );

        return bot;
    }

}
