package com.tinatiel.obschatbot.web.controller;

import com.tinatiel.obschatbot.core.client.twitch.chat.TwitchChatClientManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/client")
@RestController
public class ClientController {

    private final TwitchChatClientManager twitchChatClientManager;

    public ClientController(TwitchChatClientManager twitchChatClientManager) {
        this.twitchChatClientManager = twitchChatClientManager;
    }

    @PostMapping(path = "/twitch/start")
    void twitchStart() {
        twitchChatClientManager.startClient();
    }

    @PostMapping(path = "/twitch/stop")
    void twitchStop() {
        twitchChatClientManager.stopClient();
    }

}
