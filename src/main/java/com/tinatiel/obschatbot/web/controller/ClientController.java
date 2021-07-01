package com.tinatiel.obschatbot.web.controller;

import com.tinatiel.obschatbot.core.client.ClientManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Represents clients as a resource, encompassing actions related to managing their state and
 * settings.
 */
@RequestMapping("/api/v1/client")
@RestController
public class ClientController {

  private final ClientManager twitchChatClientManager;
  private final ClientManager obsClientManager;

  public ClientController(ClientManager twitchChatClientManager,
    ClientManager obsClientManager) {
    this.twitchChatClientManager = twitchChatClientManager;
    this.obsClientManager = obsClientManager;
  }

  @PostMapping(path = "/twitch/start")
  void twitchStart() {
    twitchChatClientManager.startClient();
  }

  @PostMapping(path = "/twitch/stop")
  void twitchStop() {
    twitchChatClientManager.stopClient();
  }

  @PostMapping(path = "/obs/start")
  void obsStart() {
    obsClientManager.startClient();
  }

  @PostMapping(path = "/obs/stop")
  void obsStop() {
    obsClientManager.stopClient();
  }

}
