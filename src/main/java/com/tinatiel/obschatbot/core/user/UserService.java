package com.tinatiel.obschatbot.core.user;

/**
 * Responsible for taking information from a partially-built User (e.g. from Twitch chat, Google
 * chat, etc.), and depending on the platform making the necessary calls to retrieve missing
 * information. Ideally, implementations should cache this information for quick retrieval.
 */
public interface UserService {

  User findUserFromPartial(User partialUserInfo);
}
