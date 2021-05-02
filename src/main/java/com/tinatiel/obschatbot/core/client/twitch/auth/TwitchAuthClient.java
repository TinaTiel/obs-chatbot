package com.tinatiel.obschatbot.core.client.twitch.auth;

public interface TwitchAuthClient {

  /**
   * Validates if the current access token is valid.
   */
  boolean isCurrentAccessTokenValid();

}
