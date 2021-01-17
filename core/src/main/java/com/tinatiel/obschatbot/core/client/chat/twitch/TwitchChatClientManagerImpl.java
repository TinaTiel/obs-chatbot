/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.chat.twitch;

import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;
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

    private final ClientFactory<PircBotX> clientFactory;
    private final ClientSettingsFactory clientSettingsFactory;

    private CompletableFuture<Void> ready;
    private CompletableFuture<Void> disconnected;
    private PircBotX bot;
    private TwitchChatSettings settings;

    public TwitchChatClientManagerImpl(ClientFactory<PircBotX> clientFactory, ClientSettingsFactory clientSettingsFactory) {
        this.clientFactory = clientFactory;
        this.clientSettingsFactory = clientSettingsFactory;
    }

    @Override
    public void start() {

        ready = new CompletableFuture<>();
        disconnected = new CompletableFuture<>();
        bot = clientFactory.generate(ready, disconnected);
        settings = clientSettingsFactory.getTwitchChatSettings();

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

}