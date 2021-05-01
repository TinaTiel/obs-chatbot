package com.tinatiel.obschatbot.core.client.twitch.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class TwitchApiClientSettings {

  private String host;
  private String usersFollowsPath;
  private String usersPath;

}
