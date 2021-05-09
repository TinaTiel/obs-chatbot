/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.model.ObsSourceVisibilityAction;
import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.core.client.obs.ObsClientManagerImpl;
import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.command.CommandRepository;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.sequencer.InOrderActionSequencer;
import com.tinatiel.obschatbot.core.sequencer.RandomOrderActionSequencer;
import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.core.user.User;
import com.tinatiel.obschatbot.core.user.local.LocalUser;
import com.tinatiel.obschatbot.core.user.local.LocalUserRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.config.EnableIntegration;

/**
 * Main entrypoint for Spring.
 */
@EnableIntegration
@SpringBootApplication
public class App {

  /**
   * Main entrypoint.
   */
  public static void main(String[] args) {
    ApplicationContext context = SpringApplication.run(App.class);

    // Register a command
    Command command = new Command()
        .name("test")
        .actionSequencer(new RandomOrderActionSequencer(Arrays.asList(
          new SendMessageAction("Test message #1, sent " + new Date()),
          new SendMessageAction("Test message #2, sent " + new Date()),
          new SendMessageAction("Test message #3, sent " + new Date())
        ), 2));

    Command pingPong = new Command()
        .name("ping")
        .actionSequencer(new InOrderActionSequencer(Arrays.asList(
          new SendMessageAction("pong!")
        ), false));

    CommandRepository commandRepository = context.getBean(CommandRepository.class);
    commandRepository.save(command);
    commandRepository.save(pingPong);

    // Register who the broadcaster is for the Twitch platform
    LocalUserRepository localUserRepository = context.getBean(LocalUserRepository.class);
    localUserRepository.save(LocalUser.builder()
      .platform(Platform.TWITCH)
      .username("tinatiel")
      .broadcaster(true)
      .build()
    );

    // Get the OBS Client manager and start it
    ClientManager obsClientManager = context.getBean("obsClientManager", ClientManager.class);
    obsClientManager.startClient();
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    RequestContext requestContext = new RequestContext(User.systemUser(), new ArrayList<>());
    Action hideAction = new ObsSourceVisibilityAction("scene1", "text1", false);
    Action showAction = new ObsSourceVisibilityAction("scene1", "text1", true);
    obsClientManager.onActionRequest(new ActionRequest(requestContext, hideAction));
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    obsClientManager.onActionRequest(new ActionRequest(requestContext, showAction));

  }
}
