/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

import com.tinatiel.obschatbot.core.ClientInstanceWrapper;

/**
 * Generates a new Client instance. Implementations should be injected with a settings store and listener.
 * @param <T> Client instance
 */
public interface ClientFactory<C,S> {

    ClientInstanceWrapper<C, S> generate();

}
