/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.messaging;

/**
 * A generic listener, generally paired with a ${@link QueueNotifier}. When the QueueNotifier
 * encounters an event, it invokes the onEvent method for each registered listener.
 *
 * @param <T> The type of event this listener processes.
 */
public interface Listener<T> {

  void onEvent(T event);
}
