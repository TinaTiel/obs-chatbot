package com.tinatiel.obschatbot.core.client.twitch.auth;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
  private String validationPath;
  private List<String> scopes;
  private String clientId;
  private String clientSecret;
  private String redirectUri;

}
