/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

public enum State {
    STARTING,       // Request was sent to start the bot
    CONNECTED,      // Bot has connected to remote
    AUTHENTICATED,  // Bot has authenticated with remote
    READY,          // Bot is ready for use
    STOPPING,       // Request was sent to stop the bot
    DISCONNECTED,   // Bot has disconnected from remote
    STOPPED,        // Bot is stopped
    ERROR           // Bot is in error; e.g. bad auth, connection, config, etc.
}
