/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request;

import com.tinatiel.obschatbot.core.remove.queue.MainQueue;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.mock;

public class CommandRequestTest {

    @Test
    void nullArgs() {

        assertThatThrownBy(() -> {
            new CommandRequest(null, new ArrayList<>());
        }).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> {
            new CommandRequest(mock(RequestContext.class),  null);
        }).isInstanceOf(IllegalArgumentException.class);

    }

}
