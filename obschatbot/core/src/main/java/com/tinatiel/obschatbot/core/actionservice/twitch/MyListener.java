package com.tinatiel.obschatbot.core.actionservice.twitch;

import org.pircbotx.hooks.events.MessageEvent;

class MyListener extends org.pircbotx.hooks.ListenerAdapter {

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        event.respondChannel("Got your message @" + event.getUser().getNick() + ": " + event.getMessage());
    }
}
