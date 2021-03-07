/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

import java.util.concurrent.BlockingQueue;

public class ClientManagerStateConsumer {

    private final BlockingQueue<StateMessage> stateQueue;
    private final Listener listener;

    public ClientManagerStateConsumer(BlockingQueue<StateMessage> stateQueue, Listener listener) {
        this.stateQueue = stateQueue;
        this.listener = listener;
    }

    State getState() {
        return State.AUTHENTICATED;
    }
}
