/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

public interface Listener {
    void onState(ClientManager clientManager, StateMessage stateMessage);
//    void onStarting(StateMessage<T> stateMessage);
//    void onConnected(StateMessage<T> stateMessage);
//    void onAuthenticated(StateMessage<T> stateMessage);
//    void onReady(StateMessage<T> stateMessage);
//    void onStopping(StateMessage<T> stateMessage);
//    void onDisconnected(StateMessage<T> stateMessage);
//    void onStopped(StateMessage<T> stateMessage);
//    void onError(StateMessage<T> stateMessage);
}
