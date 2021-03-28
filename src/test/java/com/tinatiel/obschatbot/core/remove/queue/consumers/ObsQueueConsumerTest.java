/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.remove.queue.consumers;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.client.obs.ObsClientManager;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.remove.queue.ObsQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@Disabled
class ObsQueueConsumerTest {

    ObsQueue obsQueue;
    ObsClientManager obsClientManager;

    ObsQueueConsumer consumer;

    @BeforeEach
    void setUp() {
        obsQueue = new ObsQueue();
        obsClientManager = mock(ObsClientManager.class);

        consumer = new ObsQueueConsumer(obsQueue, obsClientManager);
    }

    @Test
    void consumesContentsOfQueue() {

        // Given some OBS tasks in the queue
        ActionRequest obsCmd1 = new ActionRequest(mock(RequestContext.class), mock(Action.class));
        ActionRequest obsCmd2 = new ActionRequest(mock(RequestContext.class), mock(Action.class));
        ActionRequest obsCmd3 = new ActionRequest(mock(RequestContext.class), mock(Action.class));
        obsQueue.add(obsCmd1);
        obsQueue.add(obsCmd2);
        obsQueue.add(obsCmd3);

        // When run
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(consumer);
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.MILLISECONDS);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // Then the client manager was invoked three times
        verify(obsClientManager, times(3)).consume(any(ActionRequest.class));

    }
}