/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.remove.queue;

import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClient;
import com.tinatiel.obschatbot.core.client.obs.ObsClient;
import com.tinatiel.obschatbot.core.remove.queue.type.ActionQueueType;
import com.tinatiel.obschatbot.core.remove.queue.type.AnyActionQueueType;
import com.tinatiel.obschatbot.core.remove.queue.type.ObsActionQueueType;
import com.tinatiel.obschatbot.core.remove.queue.type.TwitchChatActionQueueType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
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
