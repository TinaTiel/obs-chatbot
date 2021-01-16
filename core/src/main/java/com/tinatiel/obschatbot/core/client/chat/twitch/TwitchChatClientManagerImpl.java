/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.chat.twitch;

import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.error.ClientNotAvailableException;
import com.tinatiel.obschatbot.core.request.queue.ActionCommand;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.util.concurrent.*;

public class TwitchChatClientManagerImpl implements TwitchChatClientManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    CompletableFuture<Void> connected;
    CompletableFuture<Void> disconnected;

    private final TwitchChatSettings settings;
    private final SSLSocketFactory sslSocketFactory;
    private PircBotX bot;

    public TwitchChatClientManagerImpl(TwitchChatSettings settings, SSLSocketFactory sslSocketFactory) {
        this.settings = settings;
        this.sslSocketFactory = sslSocketFactory;
    }

    private org.pircbotx.Configuration buildConfig() {
         return new org.pircbotx.Configuration.Builder()
                .addServer("irc.chat.twitch.tv", 6697) // Twitch's IRC url
                .setSocketFactory(sslSocketFactory)
                .addAutoJoinChannel("#" + settings.getBroadcasterChannel()) // channel is same as streamer's username
                //                 .addAutoJoinChannel("asdlfjasldheowpoasiefjl")
                .setName(settings.getUsername())             // account we're connecting as
                .setServerPassword(settings.getPassword())   // generated with TMI for now
                //                 .setServerPassword("wrong")
                .addListener(new ChatListener())   // have to register the listener!
                .setOnJoinWhoEnabled(false) // Twitch does not support WHO
                .buildConfiguration();
    }

    @Override
    public void start() {
//        connected = new CompletableFuture<>();
//        disconnected = new CompletableFuture<>();

        bot = new PircBotX(buildConfig());
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
            try {
                bot.startBot();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IrcException e) {
                e.printStackTrace();
            }
        });
//        try {
//           bot.startBot();
////            connected.get(settings.getConnectionTimeoutMs(), TimeUnit.MILLISECONDS);
//        } catch (IOException | IrcException e) {
//            log.error("Could not start Twitch Chat Bot", e);
//        } catch (InterruptedException interruptedException) {
//            log.error("Connection interrupted", interruptedException);
//        } catch (ExecutionException executionException) {
//            throw new ClientNotAvailableException("Execution failed during start", executionException);
//        } catch (TimeoutException e) {
//            throw new ClientNotAvailableException("Twitch Client failed to start within timeout (" + settings.getConnectionTimeoutMs() + "ms)", e);
//        }
        log.info("STARTED THE IRC BOT");
    }

    @Override
    public void stop() {
        bot.close();
    }

    @Override
    public void reload() {
        stop();
        start();
    }

    @Override
    public void consume(ActionCommand actionCommand) {
        log.info("Trying to consume " + actionCommand);
        try {
            if(actionCommand.getAction() instanceof SendMessageAction) {
                SendMessageAction action = (SendMessageAction) actionCommand.getAction();
                log.info("Sending message: " + action.getMessage());
                bot.sendIRC().message("#" + settings.getBroadcasterChannel(), action.getMessage()); // We must join with # + username to receive commands in that channel
                actionCommand.complete(null);
            }
        } catch (IllegalArgumentException e) {
            log.warn("Could not send message to Twitch Chat", e);
        } finally {
            actionCommand.cancel(true);
        }
    }

    private static class ChatListener extends ListenerAdapter {
        private final Logger log = LoggerFactory.getLogger(this.getClass());

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
            //event.getBot().sendCAP().request("twitch.tv/commands");

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
