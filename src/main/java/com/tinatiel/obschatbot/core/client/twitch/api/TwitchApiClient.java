package com.tinatiel.obschatbot.core.client.twitch.api;

public interface TwitchApiClient {

    boolean isFollowing(String broadcasterId, String viewerId);
}
