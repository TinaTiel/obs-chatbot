package com.tinatiel.obschatbot.core.client.twitch.chat;

import com.google.common.collect.ImmutableMap;
import com.tinatiel.obschatbot.core.client.twitch.api.TwitchApiClient;
import com.tinatiel.obschatbot.core.client.twitch.chat.messaging.TwitchClientStateMessagingGateway;
import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.command.CommandRepository;
import com.tinatiel.obschatbot.core.messaging.Listener;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.RequestConfig;
import com.tinatiel.obschatbot.core.request.handler.chat.ChatRequestHandler;
import com.tinatiel.obschatbot.core.sequencer.InOrderActionSequencer;
import com.tinatiel.obschatbot.core.user.UserConfig;
import java.util.Queue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.events.MessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.channel.interceptor.WireTap;
import org.springframework.messaging.Message;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This test verifies requests from chat ultimately are sent to the Request queue.
 */
@ContextConfiguration(classes = {RequestConfig.class, UserConfig.class})
@SpringJUnitConfig
public class TwitchChatRequestIT {

    @Autowired
    ChatRequestHandler chatRequestHandler;

    @MockBean
    CommandRepository commandRepository;

    @MockBean
    Listener<ActionRequest> twitchChatActionRequestListener;

    @MockBean
    TwitchApiClient twitchApiClient;

    @Autowired
    QueueChannel testChannel;

    @Autowired
    Queue<Message<?>> testChannelQueue;

    @Qualifier("CHANNELHERE")
    @Autowired
    AbstractMessageChannel targetChannel;

    @BeforeEach
    void setUp() {

        // Intercept messages from the lifecycle channel
        targetChannel.addInterceptor(new WireTap(testChannel));

    }

    @AfterEach
    void tearDown() {
        // Clear the channel and remove the interceptor
        testChannel.clear();
        targetChannel.removeInterceptor(0);
    }

    @Test
    void validChatRequestsAreReceivedInTheCommandRequestQueue() throws Exception {

        // Given the CommandRequest queue was empty
        assertThat(testChannelQueue).isEmpty();

        // Given a PircBotXListener (mocking the state queue client, we don't care about it here)
        PircBotxListener pircBotxListener = new PircBotxListener(
                mock(TwitchClientStateMessagingGateway.class),
                chatRequestHandler,
                new TwitchChatClientTagsParser());

        // Given a Command exists
        Command command = new Command()
                .name("foo")
                .actionSequencer(new InOrderActionSequencer(new ArrayList<>(), false));
        when(commandRepository.findByName(command.getName())).thenReturn(Optional.of(command));

        // When a message comes in from an user that could invoke that command
        User mockUser = mock(User.class);
        when(mockUser.getNick()).thenReturn("some user");
        pircBotxListener.onMessage(new MessageEvent(
                mock(PircBotX.class),
                mock(Channel.class),
                "some source",
                mock(UserHostmask.class),
                mockUser,
                "!foo",
                ImmutableMap.<String, String>builder().build()
        ));

        // And we wait reasonably
        Thread.sleep(500);

        // Then we see a CommandRequest in the queue
        assertThat(testChannelQueue).hasSize(1);

    }
}
