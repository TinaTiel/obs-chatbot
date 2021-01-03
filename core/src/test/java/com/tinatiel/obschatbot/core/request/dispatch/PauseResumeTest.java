/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.dispatch;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Fail.fail;

public class PauseResumeTest {

    @Test
    void testPauseResumeWorksAsExpected() {

        // Given three Requests that will execute a known amount of time

        // Given we submit them for execution

        // And Given we wait approximately for the first to complete

        // When we pause and then wait long enough the third task may have started

        // When we check the status of the Requests, then the third will have no been run yet

        // And then when we resume and wait long enough for the last to complete

        // Then the last Request will have run

        fail("to do");

    }
}
