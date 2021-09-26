package com.tinatiel.obschatbot.core.client.twitch.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Stores the configuration information required by the TwitchApiClient.
 */
@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class TwitchApiClientSettings {

  private String host;
  private String usersFollowsPath;
  private String usersPath;

}
