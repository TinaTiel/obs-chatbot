/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.messaging;

/**
 * A client that submits work to a queue.
 *
 * @param <T> The type submitted to the queue.
 */
public interface QueueClient<T> {

  /**
   * Submit work to a queue of type T.
   */
  void submit(T queueItem);
}
