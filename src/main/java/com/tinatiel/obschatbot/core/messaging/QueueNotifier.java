/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.messaging;

/**
 * Responsible for continously reading the contents of a target Queue of type T, providing a means
 * to register and remove ${@link Listener}s that will be each called for each item pulled from
 * the Queue.
 *
 * @param <T> type of items in the Queue.
 */
public interface QueueNotifier<T> {

  /**
   * Add a listener to be notifed on items pulled from the Queue.
   */
  void addListener(Listener<T> listener);

  /**
   * Remove a listener.
   */
  void removeListener(Listener<T> listener);

  /**
   * Notify Listeners with the provided Queue item.
   */
  void notifyListeners(T queueItem);

}
