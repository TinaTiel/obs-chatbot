package com.tinatiel.obschatbot.core.client.twitch.auth;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Encompasses the connection information required to authenticate with Twitch. Distributions
 * specify this information once, upon deployment, via the specified configuration properties.
 */
@Data
@Configuration
@ConfigurationProperties("com.tinatiel.twitch.auth")
public class TwitchAuthConnectionSettings {

  private String host;
  private String authorizationPath;
  private String tokenPath;
  private List<String> scopes;

}
