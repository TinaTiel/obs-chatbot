/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obs.chatbot.core.client;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.pircbotx.Configuration;
import org.pircbotx.MultiBotManager;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.SocketConnectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * This test requires a LIVE connection to Twitch. The purpose of this test is to verify it works the way
 * we assume it does. If this Test ever fails, It's a BIG deal -- ideally, this should
 * be monitored continuously via DataDog etc monitoring services so we are alerted to Twitch API behavior changes
 *
 * NOTE: This test requires environment variables that include real credentials
 * You can setup any Twitch account for this test, and then use https://twitchapps.com/tmi/ to generate a password,
 * just make sure to include the full string, e.g. your environment variables might look like this:
 * TWITCH_USER=robotiel
 * TWITCH_PASS=oauth:fiouw4o00wasdf08mycem3k78etvnw85s
 *
 * IDE NOTE: You may need to restart your IDE (for example, IntelliJ) after creating or updating your environment variables.
 *
 */
public class LiveTwitchIRCTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final String TWITCH_USER = "TWITCH_USER";
    private final String TWITCH_PASS = "TWITCH_PASS";
    Configuration.Builder buildConfiguration;
    String username;
    String password;
    String badUsername;
    String badPassword;

    @BeforeEach
    void setUp() {

        // Get the credentials from the system environment
        badUsername = RandomStringUtils.random(50);
        badPassword = RandomStringUtils.random(50);
        username = System.getenv(TWITCH_USER);
        password = System.getenv(TWITCH_PASS);
        assertThat(username).withFailMessage(() -> "Expecting environment variable: " + TWITCH_USER).isNotNull();
        assertThat(password).withFailMessage(() -> "Expecting environment variable: " + TWITCH_PASS).isNotNull();

        buildConfiguration = new Configuration.Builder()
                .addServer("irc.chat.twitch.tv", 6697)
                .setSocketFactory(new UtilSSLSocketFactory())
                .addAutoJoinChannel("#" + username)
                .setName(username)
                .setServerPassword(password)
                .setOnJoinWhoEnabled(false); // Twitch does not support WHO

    }

    @Test
    void twitchHostAndPortAreAsExpected() throws InterruptedException {

        // Given we provide valid host and port information, but invalid username
        CompletableFuture<Void> callback = new CompletableFuture<>();
        MultiBotManager manager = new MultiBotManager();
        manager.addBot(buildConfiguration
                .setName(badUsername)
                .setServerPassword(badPassword)
                .addListener(new Listener() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        if(event instanceof SocketConnectEvent) {
                            callback.complete(null);
                        }
                    }
                }).buildConfiguration()
        );

        // When we connect with the bot
        manager.start();

        // And then we wait a reasonable amount of time
        Thread.sleep(500);

        // And then terminate it
        manager.stop();

        // Then the SocketConnect event should have invoked the callback
        assertThat(callback)
                .withFailMessage("Twitch did not respond at expected host and port: " + buildConfiguration.getServers())
                .isCompleted();

    }

    @Test
    void whenBadAuthThenTwitchRespondsAsExpected() throws InterruptedException {

        // Given we connect to Twitch with a known account, but with bad credentials
        CompletableFuture<Void> callback = new CompletableFuture<>();
        MultiBotManager manager = new MultiBotManager();
        manager.addBot(buildConfiguration
                .setName(badUsername)
                .setServerPassword(badPassword)
                .addListener(new Listener() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        if(event instanceof NoticeEvent) {
                            NoticeEvent theEvent = (NoticeEvent) event;
                            if(theEvent.getMessage().contains("auth")) callback.complete(null);
                        }
                    }
                }).buildConfiguration()
        );

        // When we connect with the bot
        manager.start();

        // And then we wait a reasonable amount of time
        Thread.sleep(3000);

        // And then terminate it
        manager.stop();

        // Then the OnNotice event should have invoked the callback
        assertThat(callback)
                .withFailMessage("Twitch did not respond as expected when logging in with bad credentials")
                .isCompleted();

    }

    @Test
    void whenGoodAuthThenTwitchRespondsAsExpected() throws InterruptedException, ExecutionException {

        // Given we connect to Twitch with a known account and (assumed) good credentials
        CompletableFuture<Void> preconditionsFailed = new CompletableFuture<>();
        CompletableFuture<Void> callback = new CompletableFuture<>();
        MultiBotManager manager = new MultiBotManager();
        manager.addBot(buildConfiguration
                .addListener(new Listener() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        if(event instanceof NoticeEvent) {
                            preconditionsFailed.complete(null);
                        }
                        if(event instanceof JoinEvent) {
                            JoinEvent theEvent = (JoinEvent) event;
                            assertThat(theEvent.getUser().getLogin()).isEqualTo(username);
                            assertThat(theEvent.getUser().getNick()).isEqualTo(username);
                            assertThat(theEvent.getChannel().getName()).isEqualTo("#" + username);
                            callback.complete(null);
                        }
                    }
                }).buildConfiguration()
        );

        // When we connect with the bot
        manager.start();

        // And then we wait a reasonable amount of time
        Thread.sleep(5000);

        // And then terminate it
        manager.stop();

        // Then the preconditions should not have failed
        assertThat(preconditionsFailed)
                .withFailMessage(() -> String.format("Possible false-negative; verify your credentials for user '%s' and re-run the test", buildConfiguration.getName()))
                .isNotCompleted();

        // Then the OnJoin event should have invoked the callback
        assertThat(callback)
                .withFailMessage("Twitch did not respond as expected when logging in with good credentials")
                .isCompleted();

    }

    @Disabled
    @Test
    void logAllEventsThroughSuccessfulLogin() throws IOException {

        MultiBotManager manager = new MultiBotManager();
        manager.addBot(buildConfiguration
                .addListener(new Listener() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        log.info("Logged Event: \n" + event);
                    }
                }).buildConfiguration()
        );

        // When we connect with the bot
        manager.start();

        // Wait forever, so you can experiment/play around
        // BUT BEWARE, credentials ARE going to be logged
        System.in.read();

    }
}
