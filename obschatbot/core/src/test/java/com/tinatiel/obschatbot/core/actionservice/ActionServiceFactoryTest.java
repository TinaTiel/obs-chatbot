package com.tinatiel.obschatbot.core.actionservice;

import com.tinatiel.obschatbot.core.action.ActionType;
import com.tinatiel.obschatbot.core.actionservice.obs.ObsClient;
import com.tinatiel.obschatbot.core.actionservice.chat.twitch.TwitchChatClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class ActionServiceFactoryTest {

    ObsClient obsClient;
    TwitchChatClient twitchChatClient;

    ActionServiceFactory factory;

    @BeforeEach
    void setUp() {

        obsClient = mock(ObsClient.class);
        twitchChatClient = mock(TwitchChatClient.class);

        factory = new ActionServiceFactoryImpl(obsClient, twitchChatClient);

    }

    @Test
    void nullArgThrowsException() {

        assertThatThrownBy(() -> {
            factory.getService(null);
        }).isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void returnObsClient() {
        assertThat(factory.getService(ActionType.OBS))
                .isInstanceOf(ObsClient.class)
                .isEqualTo(obsClient);
    }

    @Test
    void returnTwitchChatClient() {
        assertThat(factory.getService(ActionType.TWITCH_CHAT))
                .isInstanceOf(TwitchChatClient.class)
                .isEqualTo(twitchChatClient);
    }

}
