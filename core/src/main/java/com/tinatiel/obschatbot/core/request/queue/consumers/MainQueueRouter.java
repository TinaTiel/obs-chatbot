/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.queue.consumers;

import com.tinatiel.obschatbot.core.request.queue.MainQueue;
import com.tinatiel.obschatbot.core.request.queue.ObsQueue;
import com.tinatiel.obschatbot.core.request.queue.TwitchChatQueue;

public class MainQueueRouter implements ActionQueueConsumer {

    private final MainQueue mainQueue;
    private final ObsQueue obsQueue;
    private final TwitchChatQueue twitchChatQueue;

    public MainQueueRouter(MainQueue mainQueue, ObsQueue obsQueue, TwitchChatQueue twitchChatQueue) {
        this.mainQueue = mainQueue;
        this.obsQueue = obsQueue;
        this.twitchChatQueue = twitchChatQueue;
    }

    @Override
    public void run() {
        
    }
}
