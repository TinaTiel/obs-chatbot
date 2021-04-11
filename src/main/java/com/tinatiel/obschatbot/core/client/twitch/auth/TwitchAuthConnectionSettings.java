package com.tinatiel.obschatbot.core.client.twitch.auth;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

//@ToString
//@Getter
//@AllArgsConstructor
//@Builder

@Data
@Configuration
@ConfigurationProperties("com.tinatiel.twitch.auth")
public class TwitchAuthConnectionSettings {

    private String host;
    private String authorizationPath;
    private String tokenPath;
    private List<String> scopes;

}
