/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.dispatch;

import com.tinatiel.obschatbot.core.request.Request;

/**
 * Executes a (Command) Request, validating the Runnable is a Request object
 * before attempting to execute it.
 *
 * Also provides factory and informational methods, such as providing a new SequentialExecutor
 * or returning the configured request timeout.
 */
public interface CommandExecutorService extends Pausable {


    /**
     * Gets the maximum time a pause will be in effect before automatically resuming. If negative, then it will be forever.
     * @return milliseconds timeout
     */
    long getPauseTimeoutMs();

    /**
     * Gets the maximum number of commands allowed to be executed simultaneously.
     * @return number of maximum concurrent commands.
     */
    int getMaxConcurrentCommands();

    /**
     * Submits a com.tinatiel.obschatbot.core.request.Request for execution.
     * @param request
     */
    void submit(Request request);
}