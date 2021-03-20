/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.queue.consumers;

import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.core.client.obs.ObsClientManager;
import com.tinatiel.obschatbot.core.request.queue.ActionCommand;
import com.tinatiel.obschatbot.core.request.queue.ObsQueue;

public class ObsQueueConsumer implements Runnable {

    private final ObsQueue obsQueue;
    private final ClientManager obsClientManager;

    public ObsQueueConsumer(ObsQueue obsQueue, ClientManager obsClientManager) {
        this.obsQueue = obsQueue;
        this.obsClientManager = obsClientManager;
    }

    @Override
    public void run() {
        while(true) {
            try{
                ActionCommand actionCommand = obsQueue.take();
                obsClientManager.consume(actionCommand);
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
