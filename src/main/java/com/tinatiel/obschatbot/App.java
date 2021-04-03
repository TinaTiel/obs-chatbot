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

        // Get the Twitch Client Manager, and start it
        ClientManager chatClientManager = context.getBean(TwitchChatClientManager.class);
        chatClientManager.startClient();

        // Wait for it to start
        try {
            System.out.println("Waiting for startup");
            Thread.sleep(6000);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // Register a command
        Command command = new Command()
                .actionSequencer(new InOrderActionSequencer(Arrays.asList(
                        new SendMessageAction("Test message #1, sent " + new Date()),
                        new SendMessageAction("Test message #2, sent " + new Date()),
                        new SendMessageAction("Test message #3, sent " + new Date()) // this may not execute due to short wait before stopping client
                ), false))
                .name("test");
        CommandRepository commandRepository = context.getBean(CommandRepository.class);
        commandRepository.save(command);

        // Request the command execute (using the chat handler as entrypoint)
        System.out.println("Sending a test message");
        User user = new User(Platform.TWITCH, "mango", UserType.MODERATOR, new HashSet<>());
        ChatRequestHandler chatRequestHandler = context.getBean(ChatRequestHandler.class);
        chatRequestHandler.handle(user, "!test");

        try {
            System.out.println("Waiting for command to execute");
            Thread.sleep(2000);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        System.out.println("Stopping the client");
        chatClientManager.stopClient();

        // Wait for it to stop
        try {
            System.out.println("Waiting 2 seconds");
            Thread.sleep(2000);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        chatClientManager.startClient(); // start it again

        // Wait for it to start
        try {
            System.out.println("Waiting 2 seconds");
            Thread.sleep(2000);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // stop the client
        chatClientManager.stopClient();
    }
}
