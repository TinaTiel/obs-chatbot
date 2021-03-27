/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.remove.queue;

import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.remove.queue.type.ActionQueueType;

import java.util.concurrent.BlockingQueue;

public interface AAcceptsQueueDelegator extends BlockingQueue<ActionRequest> {
    ActionQueueType getActionQueueType();
}
