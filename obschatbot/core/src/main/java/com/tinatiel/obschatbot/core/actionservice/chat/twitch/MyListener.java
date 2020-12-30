/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.actionservice.chat.twitch;

import org.pircbotx.hooks.events.MessageEvent;

class MyListener extends org.pircbotx.hooks.ListenerAdapter {

    private final FooService fooService;

    public MyListener(FooService fooService) {
        this.fooService = fooService;
    }

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        //event.respondChannel("Got your message @" + event.getUser().getNick() + ": " + event.getMessage());
        event.respondChannel(fooService.processMessage(event.getMessage()));
    }
}
