/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.queue.consumers;

import com.tinatiel.obschatbot.core.client.ActionClient;
import com.tinatiel.obschatbot.core.client.obs.ObsClient;
import com.tinatiel.obschatbot.core.request.queue.ActionCommand;
import com.tinatiel.obschatbot.core.request.queue.MainQueue;
import com.tinatiel.obschatbot.core.request.queue.ObsQueue;
import com.tinatiel.obschatbot.core.request.queue.TwitchChatQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainQueueRouter implements ActionQueueConsumer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

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
        while(true) {
            try {
                ActionCommand command = mainQueue.take();
                Class<? extends ActionClient> recipient = command.getRecipient();
                if(obsQueue.getActionQueueType().canAccept(recipient)) {
                    obsQueue.add(command);
                    log.debug("Moved to OBS Queue: " + command);
                } else if(twitchChatQueue.getActionQueueType().canAccept(recipient)) {
                    twitchChatQueue.add(command);
                    log.debug("Moved to TwitchChat Queue: " + command);
                } else {
                    log.debug("Ignored command: " + command);
                }

            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
