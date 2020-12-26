package com.tinatiel.obschatbot.core.actionservice.twitch;

import org.pircbotx.PircBotX;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.hooks.Listener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLSocketFactory;

@Configuration
public class TwitchChatClientConfig {

    @Value("${TWITCH_AUTH}")
    private String twitchAuth;

    @Bean
    PircBotX twitchChatClient() {
        org.pircbotx.Configuration config = new org.pircbotx.Configuration.Builder()
                .addServer("irc.chat.twitch.tv", 6697) // Twitch's IRC url
                .setSocketFactory(sslSocketFactory())
                .addAutoJoinChannel("#tinatiel") // channel is same as streamer's username
                .setName("robotiel")             // account we're connecting as
                .setServerPassword(twitchAuth)   // generated with TMI for now
                .addListener(myListener())   // have to register the listener!
                .buildConfiguration();

        return new PircBotX(config);
    }

    @Bean
    SSLSocketFactory sslSocketFactory() {
        return new UtilSSLSocketFactory();
    }

    @Bean
    Listener myListener() { return new MyListener(fooService()); }

    @Bean
    FooService fooService() { return new FooServiceImpl(); }

}
