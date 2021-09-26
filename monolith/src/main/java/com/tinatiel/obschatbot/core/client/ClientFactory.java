/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

/**
 * Generates a new Client instance delegate. Delegate implementations will contain a new client
 * instance, a reference to the settings used to create the client instance, and should wrap
 * important client methods.
 */
public interface ClientFactory<C, S> {

  ClientDelegate<C, S> generate();

}
