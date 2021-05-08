/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client;

import com.tinatiel.obschatbot.core.request.ActionRequest;

/**
 * Encapsulates wrapping logic around executing an ActionRequest against a client implementation.
 */
public interface ActionCommandConsumer<C> {

  void consume(C client, ActionRequest actionRequest);
}
