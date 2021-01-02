/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.dispatch;

import java.util.concurrent.*;

public class SequentialExecutor extends ThreadPoolExecutor {
    private final ExecutorService parentService;
    public SequentialExecutor(ExecutorService parentService) {
        super(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        this.parentService = parentService;
    }
}
