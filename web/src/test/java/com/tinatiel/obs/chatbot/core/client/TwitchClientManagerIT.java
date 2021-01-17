/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obs.chatbot.core.client;

import com.tinatiel.obschatbot.App;
import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClientManager;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatClientManagerImpl;
import com.tinatiel.obschatbot.core.client.chat.twitch.TwitchChatSettings;
import com.tinatiel.obschatbot.core.error.ClientNotAvailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@Import({App.class})
@ExtendWith(SpringExtension.class)
public class TwitchClientManagerIT {

    @Autowired
    TwitchChatClientManager clientManager;

    @MockBean
    ClientSettingsFactory clientSettingsFactory;

    @Value("${TWITCH_AUTH:noauth}")
    private String twitchAuth;

    @Test
    void exerciseConnectDisconnect() {

    }


}
