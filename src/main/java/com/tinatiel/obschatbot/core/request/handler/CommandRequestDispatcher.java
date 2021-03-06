/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.handler;

import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.request.RequestContext;

/**
 * Responsible for building, validating, and submitting a Request to the CommandRequest queue.
 */
public interface CommandRequestDispatcher {

  void submit(Command command, RequestContext requestContext);
}
