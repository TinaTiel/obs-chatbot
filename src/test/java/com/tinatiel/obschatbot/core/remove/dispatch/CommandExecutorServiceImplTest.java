/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.remove.dispatch;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;

public class CommandExecutorServiceImplTest {

    @Test
    void numberConcurrentCommandsMustBeGreaterThanZero() {
        assertThatThrownBy(() -> {
            new CommandExecutorServiceImpl(123, -1);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("commands");
    }

//    @Test
//    void createNewSequentialExecutor() {
//
//        // Given a CommandExecutorService instance
//        CommandExecutorService executorService = new CommandExecutorServiceImpl(
//                123, 456, 5);
//
//        // When a new sequential executor is generator
//        SequentialExecutor result = executorService.newSequentialExecutor();
//
//        // Then it has the expected properties
//        assertThat(result).isNotNull().isInstanceOf(SequentialExecutor.class);
//
//    }
}
