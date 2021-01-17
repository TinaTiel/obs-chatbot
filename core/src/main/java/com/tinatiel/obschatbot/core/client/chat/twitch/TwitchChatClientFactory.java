/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.chat.twitch;

import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSocketFactory;
import java.util.concurrent.CompletableFuture;

public class TwitchChatClientFactory implements ClientFactory<PircBotX> {

    private final ClientSettingsFactory clientSettingsFactory;
    private final SSLSocketFactory sslSocketFactory;

    public TwitchChatClientFactory(ClientSettingsFactory clientSettingsFactory, SSLSocketFactory sslSocketFactory) {
        this.clientSettingsFactory = clientSettingsFactory;
        this.sslSocketFactory = sslSocketFactory;
    }

    @Override
    public PircBotX generate(CompletableFuture<Void> ready, CompletableFuture<Void> disconnected) {
        TwitchChatSettings settings = clientSettingsFactory.getTwitchChatSettings();
        PircBotX bot = new PircBotX(new org.pircbotx.Configuration.Builder()
                .addServer(settings.getHost(), settings.getPort()) // Twitch's IRC url
                .setSocketFactory(sslSocketFactory)
                .addAutoJoinChannel("#" + settings.getBroadcasterChannel()) // channel is same as streamer's username
                //                 .addAutoJoinChannel("asdlfjasldheowpoasiefjl")
                .setName(settings.getUsername())             // account we're connecting as
                .setServerPassword(settings.getPassword())   // generated with TMI for now
                //                 .setServerPassword("wrong")
                .addListener(new ChatListener(ready, disconnected))   // have to register the listener!
                .setOnJoinWhoEnabled(false) // Twitch does not support WHO
                .buildConfiguration()
        );

        return bot;
    }

    private static class ChatListener extends ListenerAdapter {
        private final Logger log = LoggerFactory.getLogger(this.getClass());

        private final CompletableFuture<Void> ready;
        private final CompletableFuture<Void> disconnected;

        public ChatListener(CompletableFuture<Void> ready, CompletableFuture<Void> disconnected) {
            this.ready = ready;
            this.disconnected = disconnected;
        }

        //        private final CompletableFuture<Void> connected;
//        private final CompletableFuture<Void> disconnected;

//        public ChatListener(CompletableFuture<Void> connected, CompletableFuture<Void> disconnected) {
//            this.connected = connected;
//            this.disconnected = disconnected;
//        }

        @Override
        public void onConnect(ConnectEvent event) throws Exception { // Connecting to the IRC server (no auth yet)
            log.info("ON CONNECT event: " + event);
        }

        @Override
        public void onJoin(JoinEvent event) throws Exception { // joining the channel
            log.info("ON JOIN event: " + event);

            // Request tags capability so we can determine if a mod, subscriber, etc.
            event.getBot().sendCAP().request("twitch.tv/tags");

            // Request commands capability so we can respond to RECONNECT if issued by Twitch IRC server
            event.getBot().sendCAP().request("twitch.tv/commands");

            event.getBot().sendIRC().message("#tinatiel", "Obs Chatbot has joined the chat!");

        }

        @Override
        public void onNotice(NoticeEvent event) throws Exception {
            log.info("ON NOTICE event: " + event);
        }

        @Override
        public void onException(ExceptionEvent event) throws Exception {
            log.error("Exception occurred on event " + event, event.getException());
        }

        @Override
        public void onMessage(MessageEvent event) throws Exception {
            log.debug("ON MESSAGE event: " + event);
            log.info("Received Message from user '" + event.getUser().getNick() + "' : '" + event.getMessage() + "'");
            log.info("Tags: " + event.getTags());
        }

    }
}
