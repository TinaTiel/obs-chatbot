/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

import java.util.concurrent.CompletableFuture;

/**
 * Generates a new Client that will be blocking until ready, and blocking until disconnected
 * @param <T> Client instance
 */
public interface ClientFactory<T> {
    /**
     *
     * @param ready
     * @param disconnected
     * @return
     */
    T generate(CompletableFuture<Void> ready, CompletableFuture<Void> disconnected);
}
