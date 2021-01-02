/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot;

import com.tinatiel.obschatbot.core.action.model.ObsSourceVisibilityAction;
import com.tinatiel.obschatbot.core.action.runnable.RunnableObsSourceVisibilityAction;
import com.tinatiel.obschatbot.core.client.ActionClientFactory;
import com.tinatiel.obschatbot.core.client.obs.ObsClient;
import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.core.request.ObsChatbotRequestContext;
import com.tinatiel.obschatbot.core.user.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(App.class);
//        ObsClient client = context.getBean(ObsClient.class);
//        client.connect();
//        client.setSourceVisibility("Scene", "someTextSource", false);
//
//        ActionServiceFactory factory = context.getBean(ActionServiceFactory.class);
//
//        Action action = new ObsSourceVisibilityAction(
//                new ActionContext("", "", new ArrayList<>()), factory,
//                "Scene",
//                "someTextSource",
//                false
//        );
//        action.run();

//        CommandRepository commandRepository = context.getBean(CommandRepository.class);
//        CommandExpander commandExpander = context.getBean(CommandExpander.class);
//        User user = new User(Platform.TWITCH, "test");
//        commandRepository.findByName("foo")
//                .ifPresent(cmd -> {
//                    commandExpander.expand(cmd).stream()
//                            .forEach(action -> {
//                                action.run();
//                            });
//                });

//        System.out.println("You should see this message in the logs");
//        TwitchChatClient client = context.getBean(TwitchChatClient.class);
//        try {
//            Thread.sleep(3000); // wait for connection to establish
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        client.sendMessage("FOOOOO");

//        ActionClientFactory actionClientFactory = context.getBean(ActionClientFactory.class);
//        ObsSourceVisibilityAction action = new ObsSourceVisibilityAction("foo", "bar", true);
//        User user = new User(Platform.TWITCH, "mango");
//        ObsChatbotRequestContext requestContext = new ObsChatbotRequestContext(user, new ArrayList<>());
//        RunnableObsSourceVisibilityAction foo = new RunnableObsSourceVisibilityAction(
//                action,
//                (ObsClient) actionClientFactory.getService(action.getActionType()),
//                requestContext
//        );


    }
}
