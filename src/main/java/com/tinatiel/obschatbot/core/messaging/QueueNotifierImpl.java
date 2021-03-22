/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.messaging;

import java.util.concurrent.BlockingQueue;

public class QueueNotifierImpl implements QueueNotifier {

    private final PausableQueueNotifier delegate;

    public QueueNotifierImpl(BlockingQueue queue) {
        this.delegate = new PausableQueueNotifierImpl(queue);
        delegate.consume();
    }

    @Override
    public void addListener(Listener listener) {
        delegate.addListener(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        delegate.removeListener(listener);
    }

    @Override
    public void notifyListeners(Object queueItem) {
        delegate.notifyListeners(queueItem);
    }
}
