/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.chat.twitch;

public class TwitchChatSettings {

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String broadcasterChannel;
    private final long connectionTimeoutMs;

    public TwitchChatSettings(String host, int port, String username, String password, String broadcasterChannel, long connectionTimeoutMs) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.broadcasterChannel = broadcasterChannel;
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getBroadcasterChannel() {
        return broadcasterChannel;
    }

    public long getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }
}
