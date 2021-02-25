/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.chat.twitch;

import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;
import com.tinatiel.obschatbot.core.error.ClientException;
import com.tinatiel.obschatbot.core.error.Code;
import com.tinatiel.obschatbot.core.error.UnexpectedException;
import com.tinatiel.obschatbot.core.request.queue.ActionCommand;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.concurrent.*;

public class TwitchChatClientManagerImpl implements TwitchChatClientManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ClientFactory<PircBotX> clientFactory;
    private final ClientSettingsFactory clientSettingsFactory;

    private volatile CompletableFuture<Void> ready;
    private volatile CompletableFuture<Void> disconnected;
    private volatile PircBotX bot;
    private TwitchChatSettings settings;

    public TwitchChatClientManagerImpl(ClientFactory<PircBotX> clientFactory, ClientSettingsFactory clientSettingsFactory) {
        this.clientFactory = clientFactory;
        this.clientSettingsFactory = clientSettingsFactory;
    }

    @Override
    public void start() throws ClientException {

        // initialize the startup states
        ready = new CompletableFuture<>();
        disconnected = new CompletableFuture<>();
        bot = clientFactory.generate(ready, disconnected);
        settings = clientSettingsFactory.getTwitchChatSettings();

        // Define the task to start the bot. Many things can go wrong that we want to catch
        // so that we can pass this to the user and help them troubleshoot
        Callable<Void> exceptionThrowingStartBotTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    bot.startBot();
                    return null;
                } catch (ConnectException connectException) {
                    throw new ClientException(Code.CLIENT_UNREACHABLE, "Could not connect with Twitch IRC over the network; check Twitch's uptime status, or your internet connection.", connectException);
                } catch (UnknownHostException unknownHostException) {
                    throw new ClientException(Code.CLIENT_UNREACHABLE, "Could not contact Twitch IRC, the host is unknown. This should not happen, contact the developer!", unknownHostException);
                } catch (IOException | IrcException e) {
                    throw new UnexpectedException("Unexpected IRC or IO exception during Twitch Chat Client startup", e);
                }
            }
        };

        // Try to start the bot
        try {
            ExecutorService executorService = Executors.newCachedThreadPool();
            executorService.submit(exceptionThrowingStartBotTask).get(settings.getConnectionTimeoutMs(), TimeUnit.MILLISECONDS);
            ready.get(settings.getConnectionTimeoutMs(), TimeUnit.MILLISECONDS);
            log.info("Successfully started the Twitch Chat Bot");
        }
        // Catch any thrown exceptions from starting the bot
        catch (ExecutionException e) {
            if (e.getCause() instanceof ClientException) {
                log.warn("Could not start Twitch Chat Client", e);
                throw (ClientException) e.getCause();
            } else {
                log.error("Encountered unexpected problems while starting the Twitch Chat Client", e);
                stop();
            }
        }
        // Finally catch any interrupts or timeouts
        catch (InterruptedException | TimeoutException e) {
            log.error("Unable to start Twitch Chat Bot after " + settings.getConnectionTimeoutMs() + "ms", e);
            stop();
        }

    }

    @Override
    public void stop() {
        bot.close();
        log.info("Successfully stopped the Twitch Chat Bot");
    }

    @Override
    public void reload() throws ClientException {
        log.info("Reloading the Twitch Chat Bot");
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
