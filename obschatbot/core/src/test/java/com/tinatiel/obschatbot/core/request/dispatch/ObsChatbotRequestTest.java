/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.dispatch;

import com.tinatiel.obschatbot.core.request.ObsChatbotRequest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.mock;

public class ObsChatbotRequestTest {

    private final ExecutorService parentExecutor = mock(ExecutorService.class);

    @Test
    void nullArgs() {

        assertThatThrownBy(() -> {
            new ObsChatbotRequest(null, 123, new ArrayList<>());
        }).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> {
            new ObsChatbotRequest(new SequentialExecutor(parentExecutor), 123, null);
        }).isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void timeoutMustBeGreaterThanZero() {

        assertThatThrownBy(() -> {
            new ObsChatbotRequest(new SequentialExecutor(parentExecutor), -1, new ArrayList<>());
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("timeout");

        assertThatThrownBy(() -> {
            new ObsChatbotRequest(new SequentialExecutor(parentExecutor), 0, new ArrayList<>());
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("timeout");;

    }

}
