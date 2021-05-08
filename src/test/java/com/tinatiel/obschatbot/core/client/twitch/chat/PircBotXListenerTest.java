package com.tinatiel.obschatbot.core.client.twitch.chat;

import com.google.common.collect.ImmutableMap;
import com.tinatiel.obschatbot.core.client.event.ClientErrorEvent;
import com.tinatiel.obschatbot.core.client.twitch.chat.messaging.TwitchClientStateMessagingGateway;
import com.tinatiel.obschatbot.core.request.handler.chat.ChatRequestHandler;
import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.core.user.User;
import com.tinatiel.obschatbot.core.user.UserSecurityDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NoticeEvent;

import java.time.Period;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PircBotXListenerTest {

    PircBotxListener pircBotxListener;

    TwitchClientStateMessagingGateway stateClient;
    ChatRequestHandler chatRequestHandler;
    TwitchChatClientTagsParser tagsParser;

    @BeforeEach
    void setUp() {

        stateClient = mock(TwitchClientStateMessagingGateway.class);
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
        when(tagsParser.getUserIdFromTags(any())).thenReturn("123456");

        // When invoked
        pircBotxListener.onMessage(messageEvent);

        // Then the chat request handler is invoked with the user info:
        //  - platform is Twitch
        //  - username is Nick
        //  - securityDetails are from tag parser
        User expectedUser = User.builder()
                .platform(Platform.TWITCH)
                .username("foo")
                .id("123456")
                .userSecurityDetails(userSecurityDetails)
                .build();

        // (we must capture the User arg because eq() will not check the securityDetails)
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(chatRequestHandler).handle(userArgumentCaptor.capture(), eq("message"));
        assertThat(userArgumentCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expectedUser);

    }

    @Test
    void improperlyFormattedAuth() throws Exception {

        // Given Twitch sends a notice event (during first login)
        String noticeMessage = "Improperly formatted auth";
        NoticeEvent noticeEvent = new NoticeEvent(
                mock(PircBotX.class),
                mock(UserHostmask.class),
                mock(org.pircbotx.User.class),
                mock(Channel.class),
                "some channel source",
                noticeMessage,
                ImmutableMap.<String, String>builder().build()
        );

        // When the event is submitted
        pircBotxListener.onNotice(noticeEvent);

        // Then a ClientError is emitted
        // (we must capture the User arg because eq() will not check the securityDetails)
        ArgumentCaptor<ClientErrorEvent> stateEventCaptor = ArgumentCaptor.forClass(ClientErrorEvent.class);
        verify(stateClient).submit(stateEventCaptor.capture());
        assertThat(stateEventCaptor.getValue().getMessage()).isEqualTo(
                "Unable to connect to Twitch: Improperly formatted auth, contact the developer!"
        );

    }

    @Test
    void wrongTokenOrInsuficientScopes() throws Exception {

        // Given Twitch sends a notice event (during first login)
        String noticeMessage = "login unsuccessful";
        NoticeEvent noticeEvent = new NoticeEvent(
                mock(PircBotX.class),
                mock(UserHostmask.class),
                mock(org.pircbotx.User.class),
                mock(Channel.class),
                "some channel source",
                noticeMessage,
                ImmutableMap.<String, String>builder().build()
        );

        // When the event is submitted
        pircBotxListener.onNotice(noticeEvent);

        // Then a ClientError is emitted
        // (we must capture the User arg because eq() will not check the securityDetails)
        ArgumentCaptor<ClientErrorEvent> stateEventCaptor = ArgumentCaptor.forClass(ClientErrorEvent.class);
        verify(stateClient).submit(stateEventCaptor.capture());
        assertThat(stateEventCaptor.getValue().getMessage()).isEqualTo(
                "Unable to connect to Twitch: Not an User Token; contact the developer!"
        );

    }

    @Test
    void tokenRevokedDuringSession() throws Exception {

        // Given Twitch sends a notice event (during active session)
        String noticeMessage = "You don't have permission to perform that action";
        NoticeEvent noticeEvent = new NoticeEvent(
                mock(PircBotX.class),
                mock(UserHostmask.class),
                mock(org.pircbotx.User.class),
                mock(Channel.class),
                "some channel source",
                noticeMessage,
                ImmutableMap.<String, String>builder().build()
        );

        // When the event is submitted
        pircBotxListener.onNotice(noticeEvent);

        // Then a ClientError is emitted
        // (we must capture the User arg because eq() will not check the securityDetails)
        ArgumentCaptor<ClientErrorEvent> stateEventCaptor = ArgumentCaptor.forClass(ClientErrorEvent.class);
        verify(stateClient).submit(stateEventCaptor.capture());
        assertThat(stateEventCaptor.getValue().getMessage()).isEqualTo(
                "Unable to connect to Twitch: Connection (token) has been removed or revoked; re-approve and try again."
        );

    }

    @Test
    void tokenInvalidatedOutsideSession() throws Exception {

        // Given Twitch sends a notice event (during first login)
        String noticeMessage = "Login authentication failed";
        NoticeEvent noticeEvent = new NoticeEvent(
                mock(PircBotX.class),
                mock(UserHostmask.class),
                mock(org.pircbotx.User.class),
                mock(Channel.class),
                "some channel source",
                noticeMessage,
                ImmutableMap.<String, String>builder().build()
        );

        // When the event is submitted
        pircBotxListener.onNotice(noticeEvent);

        // Then a ClientError is emitted
        // (we must capture the User arg because eq() will not check the securityDetails)
        ArgumentCaptor<ClientErrorEvent> stateEventCaptor = ArgumentCaptor.forClass(ClientErrorEvent.class);
        verify(stateClient).submit(stateEventCaptor.capture());
        assertThat(stateEventCaptor.getValue().getMessage()).isEqualTo(
                "Unable to connect to Twitch: Connection (token) has been removed or revoked; re-approve and try again."
        );

    }

}
