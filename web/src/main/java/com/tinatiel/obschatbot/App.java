/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot;

import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClient;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClientManager;
import com.tinatiel.obschatbot.core.client.obs.ObsActionCommandConsumer;
import com.tinatiel.obschatbot.core.client.obs.ObsClient;
import com.tinatiel.obschatbot.core.client.obs.ObsClientManager;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.request.queue.ActionCommand;
import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.core.user.User;
import net.twasi.obsremotejava.OBSRemoteController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import com.tinatiel.obschatbot.core.action.model.ObsSourceVisibilityAction;

import java.util.ArrayList;
import java.util.Date;

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

//        ObsSourceVisibilityAction action = new ObsSourceVisibilityAction(null, "Image", false);
//        User user = new User(Platform.TWITCH, "mango");
//        RequestContext requestContext = new RequestContext(user, new ArrayList<>());
//        ActionCommand actionCommand = new ActionCommand(ObsClient.class, action, requestContext);

//        OBSRemoteController obsRemoteController = new OBSRemoteController("ws://localhost:4444", false);
//        ObsActionCommandConsumer actionCommandConsumer = new ObsActionCommandConsumer(obsRemoteController);
//        actionCommandConsumer.consume(actionCommand);

//        ObsClientManager obsClientManager = context.getBean(ObsClientManager.class);
//        obsClientManager.start();
//        obsClientManager.consume(actionCommand);
//        obsClientManager.reload();
//        obsClientManager.consume(actionCommand);
//        obsClientManager.stop();

        SendMessageAction action = new SendMessageAction("Test message " + new Date());
        User user = new User(Platform.TWITCH, "mango");
        RequestContext requestContext = new RequestContext(user, new ArrayList<>());
        ActionCommand actionCommand = new ActionCommand(action.acceptsClientType(), action, requestContext);
        TwitchChatClientManager chatClientManager = context.getBean(TwitchChatClientManager.class);
        chatClientManager.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        chatClientManager.consume(actionCommand);
//        chatClientManager.stop();

    }
}
