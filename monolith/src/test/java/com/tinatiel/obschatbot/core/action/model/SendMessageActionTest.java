/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SendMessageActionTest {

    @Test
    void messageCannotBeNull() {
        assertThatThrownBy(() -> {
            new SendMessageAction(null);
        }).isInstanceOf(IllegalArgumentException.class);
    }
}