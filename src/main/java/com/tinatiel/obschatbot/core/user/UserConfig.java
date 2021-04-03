package com.tinatiel.obschatbot.core.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {

    @Bean
    UserService userService() {
        return new UserServiceImpl();
    }

}
