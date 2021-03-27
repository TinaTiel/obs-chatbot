/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.remove.queue;

import com.tinatiel.obschatbot.core.client.ActionClient;
import com.tinatiel.obschatbot.core.error.RequestNotAcceptableException;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.remove.queue.type.ActionQueueType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Disabled
class AcceptsQueueDelegatorImplTest {

    BlockingQueue<ActionRequest> delegate;
    ActionQueueType actionQueueType;

    AcceptsQueueDelegator delegator;

    @BeforeEach
    void setUp() {
        delegate = mock(BlockingQueue.class);
        actionQueueType = mock(ActionQueueType.class);
        delegator = new AcceptsQueueDelegatorImpl(delegate, actionQueueType);
    }

    @Test
    void whenNotAcceptingActionThenThrowException() {

        // Given the delegate doesn't accept the given ActionCommand
        when(actionQueueType.canAccept(any())).thenReturn(false);

        // And given some requests
        ActionRequest single = mock(ActionRequest.class);
        List<ActionRequest> many = Arrays.asList(mock(ActionRequest.class), mock(ActionRequest.class), mock(ActionRequest.class));

        // Then insertion calls will throw exceptions
        assertThatThrownBy(() -> {
            delegator.add(single);
        }).isInstanceOf(RequestNotAcceptableException.class);

        assertThatThrownBy(() -> {
            delegator.addAll(many);
        }).isInstanceOf(RequestNotAcceptableException.class);
        assertThatThrownBy(() -> {
            delegator.offer(single);
        }).isInstanceOf(RequestNotAcceptableException.class);
        assertThatThrownBy(() -> {
            delegator.offer(single, 0L, TimeUnit.MILLISECONDS);
        }).isInstanceOf(RequestNotAcceptableException.class);
        assertThatThrownBy(() -> {
            delegator.put(single);
        }).isInstanceOf(RequestNotAcceptableException.class);
        assertThatThrownBy(() -> {
            delegator.retainAll(many);
        }).isInstanceOf(RequestNotAcceptableException.class);

        // And underlying calls will not be invoked
        verifyNoInteractions(delegate);

    }

    @Test
    void delegateInsertionToDelegateWhenActionAccepted() throws InterruptedException {

        // Given the delegate accepts the given ActionCommand
        when(actionQueueType.canAccept(any())).thenReturn(true);

        // And given some requests
        ActionRequest single = mock(ActionRequest.class);
        List<ActionRequest> many = Arrays.asList(mock(ActionRequest.class), mock(ActionRequest.class), mock(ActionRequest.class));

        // Then insertion calls will delegate to the delegate
        delegator.add(single);
        verify(delegate).add(single);

        delegator.addAll(many);
        verify(delegate).addAll(many);

        delegator.offer(single);
        verify(delegate).offer(single);

        delegator.offer(single, 0L, TimeUnit.MILLISECONDS);
        verify(delegate).offer(single, 0L, TimeUnit.MILLISECONDS);

        delegator.put(single);
        verify(delegate).put(single);

    }

    private static interface SomeClient extends ActionClient {

    }

}