/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

import com.tinatiel.obschatbot.core.client.obs.ObsClient;

/**
 * Provides a way to manage a client. Implementations should be injected with a reference to the settings directly or
 * to a data-store that provides them (settings are assumed mutable). When a client is started, a new instance is created
 * with those settings -- and when a client is stopped then the client is destroyed.
 * @param <T> Client class that is wrapped by the manager.
 */
public interface ClientManager<T> {

    /**
     * Starts a new client instance. Implementations should be blocking until timeout expires.
      */
    void start();

    /**
     * Stops a client instance. Implementations should be blocking until timeout expires.
     */
    void stop();

    /**
     * Convenience method to stop and then start a client. Since a new client is spun up on each start, this method can
     * be used when fresh settings should be loaded (the old client is destroyed, and the new client is created with the
     * updated settings.
     */
    void reload();

    /**
     * Returns the current instance of the client. WARNING - this reference can become stale due to a client being stopped
     * or reloaded.
     * @return Current instance of the client.
     */
    T getClient();
}
