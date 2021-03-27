/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.remove.dispatch;

import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.request.RequestContext;

/**
 * Responsible for building, validating, and submitting a Request to the queue for execution.
 * Also provides methods to pause/resume execution, which delegate to the underlying executor.
 */
public interface CommandDispatcher {
    void submit(Command command, RequestContext requestContext);
    CommandExecutorService getUnderlyingService();
}
