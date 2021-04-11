/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.messaging;

/**
 * A ${@link QueueNotifier} that can pause/resume consumption from its target queue.
 *
 * @param <T> the type consumed from the queue.
 */
public interface PausableQueueNotifier<T> extends QueueNotifier<T> {

  /**
   * Pauses consumption from target queue.
   */
  void pause();

  /**
   * Starts (or resumes) consumption from target queue.
   */
  void consume();
}
