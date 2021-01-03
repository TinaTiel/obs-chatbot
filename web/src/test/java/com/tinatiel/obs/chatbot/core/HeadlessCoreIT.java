/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obs.chatbot.core;

import com.tinatiel.obschatbot.App;
import com.tinatiel.obschatbot.core.action.model.ObsSourceVisibilityAction;
import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClient;
import com.tinatiel.obschatbot.core.client.obs.ObsClient;
import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.command.CommandRepository;
import com.tinatiel.obschatbot.core.request.handler.chat.ChatRequestHandler;
import com.tinatiel.obschatbot.core.sequencer.ActionSequencer;
import com.tinatiel.obschatbot.core.sequencer.InOrderActionSequencer;
import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.core.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.*;

@Import({HeadlessCoreIT.HeadlessCoreITtestConfig.class, App.class})
@ExtendWith(SpringExtension.class)
public class HeadlessCoreIT {

    @SpyBean
    ObsClient obsClient;

    @SpyBean
    TwitchChatClient twitchChatClient;

    @MockBean
    CommandRepository commandRepository;

    @Autowired
    ChatRequestHandler chatRequestHandler;

    @Test
    void generateRequestAsExpected() {

        // Given a real command with actions returned by the repository
        SendMessageAction action1 = new SendMessageAction("Check out this mango!");
        ObsSourceVisibilityAction action2 = new ObsSourceVisibilityAction(null, "mango.jpg", true);
        SendMessageAction action3 = new SendMessageAction("Isn't that neat?");
        ObsSourceVisibilityAction action4 = new ObsSourceVisibilityAction("main", "mango.jpg", false);
        SendMessageAction action5 = new SendMessageAction("Thanks for watching!");
        ActionSequencer inOrderSequencer = new InOrderActionSequencer(
                Arrays.asList(action1, action2, action3, action4, action5),
                false
        );
        Command command = new Command().name("mango").actionSequencer(inOrderSequencer);
        when(commandRepository.findByName(command.getName())).thenReturn(Optional.of(command));

        // And a real chat command
        String request = "!mango has arrived in chat! :D";

        // When executed
        User user = new User(Platform.TWITCH, "mango");
        chatRequestHandler.handle(user, request);

        // Then the actions are executed in order as expected, without errors
        // (check the logs)

        // These don't work due to Spring AOP proxies
        verify(twitchChatClient).sendMessage(action1.getMessage());
        verify(obsClient).setSourceVisibility(action2.getSceneName(), action2.getSourceName(), action2.isVisible());
        verify(twitchChatClient).sendMessage(action3.getMessage());
        verify(obsClient).setSourceVisibility(action4.getSceneName(), action4.getSourceName(), action4.isVisible());
        verify(twitchChatClient).sendMessage(action5.getMessage());

    }


    private static class TestObsClient implements ObsClient {
        private final Logger log = LoggerFactory.getLogger(this.getClass());
        Random random = new Random();

        @Override
        public void connect() {
            log.info("Connecting/Connected to OBS");
        }

        @Override
        public void setSourceVisibility(String scene, String source, boolean visibility) {
            log.info(String.format("%s source '%s' in scene '%s'",
                visibility ? "Showing" : "Hiding",
                source,
                scene == null ? "(current scene)": scene
            ));
            try {
                Thread.sleep(random.nextInt(5) + 5);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            log.info("...Done!");
        }

        @Override
        public String toString() {
            return "TestObsClient";
        }
    }

    private static class TestTwitchChatClient implements TwitchChatClient {
        private final Logger log = LoggerFactory.getLogger(this.getClass());
        Random random = new Random();

        @Override
        public void sendMessage(String message) {
            log.info("Sending message: " + message);
            try {
                Thread.sleep(random.nextInt(10) + 5);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            log.info("...Sent!");
        }

        @Override
        public String toString() {
            return "TestTwitchChatClient";
        }
    }

    @TestConfiguration
    public static class HeadlessCoreITtestConfig {

        @Primary
        @Bean
        public ObsClient testObsClient() {
            return new TestObsClient();
        }

        @Primary
        @Bean
        public TwitchChatClient testTwitchChatClient() {
            return new TestTwitchChatClient();
        }

    }

}
