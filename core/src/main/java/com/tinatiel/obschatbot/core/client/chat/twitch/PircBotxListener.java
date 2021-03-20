/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.chat.twitch;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PircBotxListener extends ListenerAdapter {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onConnect(ConnectEvent event) throws Exception { // Connecting to the IRC server (no auth yet)
        log.info("ON CONNECT event: " + event);
    }

    @Override
    public void onJoin(JoinEvent event) throws Exception { // joining the channel
        log.info("ON JOIN event: " + event);

        // Request tags capability so we can determine if a mod, subscriber, etc.
        log.debug("Requesting tags capability");
        event.getBot().sendCAP().request("twitch.tv/tags");

        // Request commands capability so we can respond to RECONNECT if issued by Twitch IRC server
        log.debug("Requesting commands capability");
        event.getBot().sendCAP().request("twitch.tv/commands");

        // Drop a welcome message into chat to provide feedback to broadcaster
        log.debug("Giving broadcaster a welcome message");
        event.getBot().sendIRC().message("#tinatiel", "Obs Chatbot has joined the chat!");

        log.debug("startup complete");
//        listener.onEvent(clientManager, new StateEvent(State.READY, "Ready"));

    }

    @Override
    public void onNotice(NoticeEvent event) throws Exception {
        log.debug("ON NOTICE event: " + event);
        if(event.getNotice().contains("auth")) {
//            listener.onEvent(clientManager, new StateEvent(State.ERROR, "Unable to start Twitch Bot, bad credentials"));
            //throw new ClientException(Code.CLIENT_BAD_CREDENTIALS, "Unable to start Twitch Bot, bad credentials", null);
        }
    }

    @Override
    public void onException(ExceptionEvent event) throws Exception {
        log.debug("Exception occurred on event " + event, event.getException());
//        listener.onEvent(clientManager, new StateEvent(State.ERROR, event.getException().getMessage()));
    }

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        log.debug("ON MESSAGE event: " + event);
        log.info("Received Message from user '" + event.getUser().getNick() + "' : '" + event.getMessage() + "'");
        log.info("Tags: " + event.getTags());
    }

}
