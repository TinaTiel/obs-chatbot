/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.queue;

import com.tinatiel.obschatbot.core.client.ActionClient;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

public interface ActionQueueType {
    boolean canAccept(Class<? extends ActionClient> recipient);
}
