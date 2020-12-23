package com.tinatiel.obschatbot.core.actionservice.twitch;

import com.tinatiel.obschatbot.core.actionservice.ActionService;

public interface TwitchChatClient extends ActionService {

    void sendMessage(String message);

}
