package com.tinatiel.obschatbot.core.client.twitch.auth;

/**
 * A Twitch client responsible for handling authentication operations not already handled
 * by Spring Security, e.g. Twitch's additional requirement to validate access tokens periodically.
 */
public interface TwitchAuthClient {

  /**
   * Validates if the current access token is valid.
   */
  boolean isCurrentAccessTokenValid();

}
