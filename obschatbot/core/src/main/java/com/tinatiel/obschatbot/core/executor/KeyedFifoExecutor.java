/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.executor;

import java.util.concurrent.Executor;

public interface KeyedFifoExecutor<T> extends Executor {
    void executeKeyedRunnable(OrderedKeyedRunnable<T> orderedKeyedRunnable);
    OrderedKeyedRunnable<T> createKeyedRunnable(Runnable runnable, T key);
}
