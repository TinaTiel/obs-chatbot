/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.remove;

import com.tinatiel.obschatbot.App;
import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.model.ObsSourceVisibilityAction;
import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.command.CommandRepository;
import com.tinatiel.obschatbot.core.request.handler.chat.ChatRequestHandler;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.remove.queue.MainQueue;
import com.tinatiel.obschatbot.core.sequencer.ActionSequencer;
import com.tinatiel.obschatbot.core.sequencer.InOrderActionSequencer;
import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.core.user.User;
import com.tinatiel.obschatbot.core.user.UserType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.*;

@Disabled
@Import({App.class})
@ExtendWith(SpringExtension.class)
public class HeadlessCoreIT {

    @MockBean
    CommandRepository commandRepository;

    @Autowired
    ChatRequestHandler chatRequestHandler;

    @Autowired
    MainQueue mainQueue;

    @MockBean
    ExecutorService queueConsumerMainExecutor; // stub out the consumer executor so we let things build in MainQueue

    @Test
    void whenActionCommandsTimeoutThenMainQueueContainsThem() {

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
        User user = new User(Platform.TWITCH, "mango", UserType.MODERATOR);
        chatRequestHandler.handle(user, request);

        // Then the expected messages are added to the main queue, in order (if we wait for them to filter through)
        try {
            System.out.println("Waiting");
            Thread.sleep(5000 + 100); // RequestConfig states timeout is 1000ms per command
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        List<Action> received =mainQueue.stream()
                .map(ActionRequest::getAction)
                .collect(Collectors.toList());
        System.out.println("Main queue: " + received);

        assertThat(received).containsExactly(action1, action2, action3, action4, action5); // Assuming the actions timeout here, since we have no consumers

    }

}
