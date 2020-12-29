package com.tinatiel.obschatbot.core.executor;

import java.util.concurrent.Executor;

public interface KeyedFifoExecutor<T> extends Executor {
    void executeKeyedRunnable(OrderedKeyedRunnable<T> orderedKeyedRunnable);
    OrderedKeyedRunnable<T> createKeyedRunnable(Runnable runnable, T key);
}
