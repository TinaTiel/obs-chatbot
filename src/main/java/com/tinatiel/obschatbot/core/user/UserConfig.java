package com.tinatiel.obschatbot.core.user;

import com.tinatiel.obschatbot.core.client.twitch.api.TwitchApiClient;
import com.tinatiel.obschatbot.core.user.local.LocalUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {

    @Autowired
    LocalUserRepository localUserRepository;

    @Autowired
    TwitchApiClient twitchApiClient;

    @Bean
    UserService userService() {
        return new UserServiceImpl(
                localUserRepository,
                twitchApiClient
        );
    }

}
