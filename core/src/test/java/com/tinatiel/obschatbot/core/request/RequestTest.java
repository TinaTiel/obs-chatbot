/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request;

import com.tinatiel.obschatbot.core.request.dispatch.SequentialExecutorImpl;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.mock;

public class RequestTest {

    private final ExecutorService parentExecutor = mock(ExecutorService.class);

    @Test
    void nullArgs() {

        assertThatThrownBy(() -> {
            new Request(null, 123, new ArrayList<>());
        }).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> {
            new Request(new SequentialExecutorImpl(parentExecutor), 123, null);
        }).isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void timeoutMustBeGreaterThanZero() {

        assertThatThrownBy(() -> {
            new Request(new SequentialExecutorImpl(parentExecutor), -1, new ArrayList<>());
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("timeout");

        assertThatThrownBy(() -> {
            new Request(new SequentialExecutorImpl(parentExecutor), 0, new ArrayList<>());
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("timeout");;

    }

}
