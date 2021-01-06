/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.queue;

import com.tinatiel.obschatbot.core.request.queue.type.AnyActionQueueType;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.IntFunction;

public class MainQueue extends AcceptsQueueDelegatorImpl {

    public MainQueue() {
        super(new LinkedBlockingQueue<>(), new AnyActionQueueType());
    }

}
