/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.dispatch;

/**
 * Executes a (Command) Request, validating the Runnable is a Request object
 * before attempting to execute it.
 *
 * Also provides factory and informational methods, such as providing a new SequentialExecutor
 * or returning the configured request timeout.
 */
public interface CommandExecutorService extends PausableExecutorService {
    SequentialExecutor newSequentialExecutor();
    long getRequestTimeoutMs();
}