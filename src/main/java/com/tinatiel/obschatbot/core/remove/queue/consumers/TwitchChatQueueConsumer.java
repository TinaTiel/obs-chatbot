/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.remove.queue.consumers;

import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.remove.queue.TwitchChatQueue;

public class TwitchChatQueueConsumer implements Runnable {

    private final TwitchChatQueue twitchQueue;
    private final ClientManager twitchClientManager;

    public TwitchChatQueueConsumer(TwitchChatQueue twitchQueue, ClientManager twitchClientManager) {
        this.twitchQueue = twitchQueue;
        this.twitchClientManager = twitchClientManager;
    }

    @Override
    public void run() {
        while(true) {
            try{
                ActionRequest actionRequest = twitchQueue.take();
                twitchClientManager.consume(actionRequest);
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
