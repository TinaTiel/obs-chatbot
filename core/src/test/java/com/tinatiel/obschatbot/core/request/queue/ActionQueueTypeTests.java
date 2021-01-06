/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.queue;

import com.tinatiel.obschatbot.core.client.ActionClient;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClient;
import com.tinatiel.obschatbot.core.client.obs.ObsClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ActionQueueTypeTests {

    @Test
    void anyQueueType() {
        ActionQueueType queueType = new AnyActionQueueType();
        assertThat(queueType.canAccept(ObsClient.class)).isTrue();
        assertThat(queueType.canAccept(TwitchChatClient.class)).isTrue();
    }

    @Test
    void obsQueueType() {
        ActionQueueType queueType = new ObsActionQueueType();
        assertThat(queueType.canAccept(ObsClient.class)).isTrue();
        assertThat(queueType.canAccept(TwitchChatClient.class)).isFalse();
    }

    @Test
    void twitchChatQueueType() {
        ActionQueueType queueType = new TwitchChatActionQueueType();
        assertThat(queueType.canAccept(ObsClient.class)).isFalse();
        assertThat(queueType.canAccept(TwitchChatClient.class)).isTrue();
    }

}
