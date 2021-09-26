/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.handler.chat;

import com.tinatiel.obschatbot.core.user.User;

/**
 * Chat Request handler is invoked with the message sent, andpartial information of an user (e.g.
 * what is provided by IRC User, or Google User, or etc. as given in the chat client before making
 * any additional API calls).
 *
 * <p>Underlying implementations should invoke a dispatcher that builds and sends a CommandRequest
 * into the request system.
 */
public interface ChatRequestHandler {

  void handle(User partialUser, String message);
}
