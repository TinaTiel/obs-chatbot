package com.tinatiel.obschatbot.core.client.twitch.chat;

import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import com.tinatiel.obschatbot.core.messaging.QueueClient;
import com.tinatiel.obschatbot.core.request.handler.chat.ChatRequestHandler;
import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.core.user.User;
import com.tinatiel.obschatbot.core.user.UserSecurityDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.pircbotx.hooks.events.MessageEvent;

import java.time.Period;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PircBotXListenerTest {

    PircBotxListener pircBotxListener;

    QueueClient<ObsChatbotEvent> stateClient;
    ChatRequestHandler chatRequestHandler;
    TwitchChatClientTagsParser tagsParser;

    @BeforeEach
    void setUp() {

        stateClient = mock(QueueClient.class);
        chatRequestHandler = mock(ChatRequestHandler.class);
        tagsParser = mock(TwitchChatClientTagsParser.class);

        pircBotxListener = new PircBotxListener(stateClient, chatRequestHandler, tagsParser);
    }

    @Test
    void onMessageTriggersChatRequestHandlerWithExpectedUserAndMessage() throws Exception {

        // Given an message event
        MessageEvent messageEvent = mock(MessageEvent.class, Mockito.RETURNS_DEEP_STUBS);
        org.pircbotx.User mockUser = mock(org.pircbotx.User.class);
        when(mockUser.getNick()).thenReturn("foo");
        when(messageEvent.getUser()).thenReturn(mockUser);
        when(messageEvent.getMessage()).thenReturn("message");

        // And given the tags parser returns info
        UserSecurityDetails userSecurityDetails = UserSecurityDetails.builder()
                .moderator(false)
                .patron(true)
                .patronPeriod(Period.ofMonths(3))
                .build();
        when(tagsParser.getDetailsFromTags(any())).thenReturn(userSecurityDetails);

        // When invoked
        pircBotxListener.onMessage(messageEvent);

        // Then the chat request handler is invoked with the user info:
        //  - platform is Twitch
        //  - username is Nick
        //  - securityDetails are from tag parser
        User expectedUser = User.builder()
                .platform(Platform.TWITCH)
                .username("foo")
                .userSecurityDetails(userSecurityDetails)
                .build();

        // (we must capture the User arg because eq() will not check the securityDetails)
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(chatRequestHandler).handle(userArgumentCaptor.capture(), eq("message"));
        assertThat(userArgumentCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expectedUser);

    }


}
