package com.tinatiel.obschatbot.core.executor;

import java.util.Queue;
import java.util.concurrent.Executor;

public interface OrderedKeyedRunnable<T> extends Runnable {
    T getKey();
    Queue<OrderedKeyedRunnable<T>> getQueue();
    Executor getDelegate();
}
