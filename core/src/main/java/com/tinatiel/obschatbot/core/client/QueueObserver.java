/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

public interface QueueObserver<T> {
    void registerListener(Listener<T> listener);
    void notifyListeners(T event);
}
