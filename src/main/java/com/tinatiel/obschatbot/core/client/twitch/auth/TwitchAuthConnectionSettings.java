package com.tinatiel.obschatbot.core.client.twitch.auth;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Encompasses the connection information required to authenticate with Twitch. Distributions
 * specify this information once, upon deployment, via the specified configuration properties.
 */
@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class TwitchAuthConnectionSettings {

  private String host;
  private String authorizationPath;
  private String tokenPath;
  private List<String> scopes;
  private String clientId;
  private String clientSecret;
  private String redirectUri;

}
