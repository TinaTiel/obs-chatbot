/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action;

import com.tinatiel.obschatbot.core.client.ActionClient;
import com.tinatiel.obschatbot.core.dispatch.CommandRequestContext;

public interface RunnableAction<T extends Action<T>> extends Runnable {
    CommandRequestContext getRequestContext();
    T getAction();
    ActionClient getClient();
}
