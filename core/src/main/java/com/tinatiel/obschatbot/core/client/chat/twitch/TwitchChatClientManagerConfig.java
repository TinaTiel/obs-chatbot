/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.chat.twitch;

import com.tinatiel.obschatbot.core.client.*;
import com.tinatiel.obschatbot.core.messaging.*;
import com.tinatiel.obschatbot.core.request.queue.ActionCommand;
import com.tinatiel.obschatbot.core.request.queue.TwitchChatQueue;
import org.pircbotx.PircBotX;
import org.pircbotx.UtilSSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLSocketFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class TwitchChatClientManagerConfig {

    @Autowired
    ClientSettingsFactory clientSettingsFactory;

    @Autowired
    TwitchChatQueue requestQueue;

    @Bean
    SSLSocketFactory sslSocketFactory() {
        return new UtilSSLSocketFactory();
    }

    @Bean
    PircBotxListener pircBotxListener() {
        return new PircBotxListener(
                stateClient(),
                requestClient()
        );
    }

    @Bean
    ClientFactory<PircBotX> twitchChatClientFactory() {
        return new TwitchChatClientFactory(
            clientSettingsFactory,
            sslSocketFactory(),
            pircBotxListener()
        );
    }

    @Bean
    ClientManager clientManagerTwitchChatImpl() {
        return new ClientManagerTwitchChatImpl(stateClient(), twitchChatClientFactory());
    }

    @Bean
    BlockingQueue<TwitchClientState> twitchChatStateQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean
    QueueClient<TwitchClientStateEvent> stateClient() {
        return new QueueClientImpl(twitchChatStateQueue());
    }

    @Bean
    QueueNotifier<TwitchClientStateEvent> notifier() {
        QueueNotifier<TwitchClientStateEvent> notifier = new QueueNotifierImpl(twitchChatStateQueue());
        notifier.addListener(eventLogger());
        notifier.addListener(clientManagerTwitchChatImpl());

        return notifier;
    }

    @Bean
    Listener<TwitchClientStateEvent> eventLogger() {
        return new Listener<TwitchClientStateEvent>() {

            private final Logger log = LoggerFactory.getLogger(this.getClass());

            @Override
            public void onEvent(TwitchClientStateEvent event) {
                log.debug(event.toString());
            }
        };
    }

    @Bean
    QueueClient<ActionCommand> requestClient() {
        return new QueueClientImpl(requestQueue);
    }

}
