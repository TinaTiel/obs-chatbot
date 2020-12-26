package com.tinatiel.obschatbot.core.actionservice.chat.twitch;

import com.tinatiel.obschatbot.core.actionservice.ActionService;

public interface TwitchChatClient extends ActionService {

    void sendMessage(String message);

}
