/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obs.chatbot.core;

import com.tinatiel.obschatbot.App;
import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.model.ObsSourceVisibilityAction;
import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClient;
import com.tinatiel.obschatbot.core.client.obs.ObsClient;
import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.command.CommandRepository;
import com.tinatiel.obschatbot.core.request.handler.chat.ChatRequestHandler;
import com.tinatiel.obschatbot.core.request.queue.ActionCommand;
import com.tinatiel.obschatbot.core.request.queue.MainQueue;
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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.*;

@Import({HeadlessCoreIT.HeadlessCoreITtestConfig.class, App.class})
@ExtendWith(SpringExtension.class)
public class HeadlessCoreIT {

//    @SpyBean
    @Autowired
    ObsClient obsClient;

//    @SpyBean
    @Autowired
    TwitchChatClient twitchChatClient;

    @Autowired
    List<String> invocations;

    @MockBean
    CommandRepository commandRepository;

    @Autowired
    ChatRequestHandler chatRequestHandler;

    @Autowired
    MainQueue mainQueue;

    @MockBean
    ExecutorService queueConsumerMainExecutor; // stub out the consumer executor so we let things build in MainQueue

    @Test
    void whenNoConsumptionThenMainQueueOnlyContainsFirstAction() {

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

        // And a chat command text
        String request = "!mango has arrived in chat! :D";

        // When handled by the chat request handler
        User user = new User(Platform.TWITCH, "mango");
        chatRequestHandler.handle(user, request);

        // Then the expected messages are added to the main queue, in order (if we wait for them to filter through)
        try {
            System.out.println("Waiting");
            Thread.sleep(5000 + 100); // RequestConfig states timeout is 1000ms per command
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        List<Action> received =mainQueue.stream()
                .map(ActionCommand::getAction)
                .collect(Collectors.toList());
        System.out.println("Main queue: " + received);

        assertThat(received).containsExactly(action1, action2, action3, action4, action5); // Assuming the actions timeout here, since we have no consumers

        // Then the actions are executed in order as expected, without errors
        // Spy Beans don't work as expected, see https://github.com/spring-projects/spring-boot/issues/7033
//        verify(twitchChatClient).sendMessage(action1.getMessage());
//        verify(obsClient).setSourceVisibility(action2.getSceneName(), action2.getSourceName(), action2.isVisible());
//        verify(twitchChatClient).sendMessage(action3.getMessage());
//        verify(obsClient).setSourceVisibility(action4.getSceneName(), action4.getSourceName(), action4.isVisible());
//        verify(twitchChatClient).sendMessage(action5.getMessage());

        // As a workaround, we manually verify using a synchronized list
//        try {
//            Thread.sleep(1500); // should be plenty of time
//        } catch (InterruptedException interruptedException) {
//            interruptedException.printStackTrace();
//        }
//        System.out.println("INVOCATIONS: \n" + invocations.stream().collect(Collectors.joining("\n")));
//        assertThat(invocations).hasSize(5);
//        assertThat(invocations.get(0)).contains(action1.getMessage());
//        assertThat(invocations.get(1)).contains(action2.getSourceName());
//        assertThat(invocations.get(2)).contains(action3.getMessage());
//        assertThat(invocations.get(3)).contains(action4.getSourceName());
//        assertThat(invocations.get(4)).contains(action5.getMessage());

    }


    private static class TestObsClient implements ObsClient {
        private final Logger log = LoggerFactory.getLogger(this.getClass());
        Random random = new Random();

        private final List<String> invocations;

        public TestObsClient(List<String> invocations) {
            this.invocations = invocations;
        }

        @Override
        public void connect() {
            log.info("Connecting/Connected to OBS");
        }

        @Override
        public void setSourceVisibility(String scene, String source, boolean visibility) {

            invocations.add(String.format("ObsClient: scene:%s, source:%s, visibility:%s", scene, source, visibility));

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

        private final List<String> invocations;

        public TestTwitchChatClient(List<String> invocations) {
            this.invocations = invocations;
        }

        @Override
        public void sendMessage(String message) {

            invocations.add(String.format("TwitchChatClient: message:%s", message));

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

        @Bean
        /**
         * Workaround for not using SpyBeans due to https://github.com/spring-projects/spring-boot/issues/7033
         */
        public List<String> invocations() {
            return Collections.synchronizedList(new ArrayList<>());
        }

        @Primary
        @Bean
        public ObsClient testObsClient() {
            return new TestObsClient(invocations());
        }

        @Primary
        @Bean
        public TwitchChatClient testTwitchChatClient() {
            return new TestTwitchChatClient(invocations());
        }

    }

}
