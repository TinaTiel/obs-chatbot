/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.obs;

public class ObsSettings {

    private String host;
    private int port;
    private String password;
    private long connectionTimeoutMs;

    public ObsSettings(String host, int port, String password, long connectionTimeoutMs) {
        this.host = host;
        this.port = port;
        this.password = password;
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getPassword() {
        return password;
    }

    public long getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }
}
