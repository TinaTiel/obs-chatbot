/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.executor;

import com.tinatiel.obschatbot.core.dispatch.CommandRequest;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executor;

public interface OrderedKeyedRunnable<T> extends Runnable {
    T getKey();
    Map<T, Queue<KeyedFifoExecutor<T>>> getSiblings();
    Executor getDelegate();
}
