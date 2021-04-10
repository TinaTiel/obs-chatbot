/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot;

import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.core.client.twitch.chat.TwitchChatClientManager;
import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.command.CommandRepository;
import com.tinatiel.obschatbot.core.request.handler.chat.ChatRequestHandler;
import com.tinatiel.obschatbot.core.sequencer.InOrderActionSequencer;
import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.core.user.User;
import com.tinatiel.obschatbot.core.user.UserSecurityDetails;
import com.tinatiel.obschatbot.core.user.UserType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(App.class);

        // Register a command
        Command command = new Command()
                .name("test")
                .actionSequencer(new InOrderActionSequencer(Arrays.asList(
                        new SendMessageAction("Test message #1, sent " + new Date()),
                        new SendMessageAction("Test message #2, sent " + new Date()),
                        new SendMessageAction("Test message #3, sent " + new Date()) // this may not execute due to short wait before stopping client
                ), false));
        Command pingPong = new Command()
                .name("ping")
                .actionSequencer(new InOrderActionSequencer(Arrays.asList(
                        new SendMessageAction("pong!")
                ), false));

        CommandRepository commandRepository = context.getBean(CommandRepository.class);
        commandRepository.save(command);
        commandRepository.save(pingPong);

//        // Get the Twitch Client Manager, and start it
//        ClientManager chatClientManager = context.getBean(TwitchChatClientManager.class);
//        chatClientManager.startClient();

    }
}
