package com.tinatiel.obschatbot.core.client.twitch.api;

/**
 * Responsible for fetching information remotely from Twitch.
 */
public interface TwitchApiClient {

  /**
   * Verifies if a given view is following the broadcaster. Note that Twitch's APIs expect
   * any user IDs to be in the form "123456" (not a GUID).
   *
   * @param broadcasterId ID of the broadcaster.
   * @param viewerId ID of the viewer.
   * @return true if the given viewer is following the broadcaster. False otherwise, or by default
   *         if Twitch cannot be reached.
   */
  boolean isFollowing(String broadcasterId, String viewerId);

  /**
   * Validates if the current access token is valid
   * @return
   */
  boolean isCurrentAccessTokenValid();

}
