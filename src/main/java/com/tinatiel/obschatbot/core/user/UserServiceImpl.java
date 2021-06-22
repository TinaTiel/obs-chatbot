package com.tinatiel.obschatbot.core.user;

import com.tinatiel.obschatbot.core.client.twitch.api.TwitchApiClient;
import com.tinatiel.obschatbot.data.localuser.model.LocalUserDto;
import com.tinatiel.obschatbot.data.localuser.LocalUserService;
import com.tinatiel.obschatbot.data.localuser.model.LocalGroupDto;
import com.tinatiel.obschatbot.security.owner.OwnerDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of the UserService that fully builds the security details etc. of a given user,
 * ideally from a cached instance, otherwise attempts to fetch information locally (for example,
 * group membership) and remotely (for example, determining if a Twitch viewer follows the
 * broadcaster).
 */
public class UserServiceImpl implements UserService {

  private final OwnerService ownerService;
  private final LocalUserService localUserService;
  private final TwitchApiClient twitchApiClient;

  public UserServiceImpl(OwnerService ownerService,
    LocalUserService localUserService, TwitchApiClient twitchApiClient) {
    this.ownerService = ownerService;
    this.localUserService = localUserService;
    this.twitchApiClient = twitchApiClient;
  }

  @Override
  public User getUserFromPartial(User partialUserInfo) {

    // Validate the minimum info is present
    if (partialUserInfo.getPlatform() == null || partialUserInfo.getUsername() == null) {
      throw new IllegalArgumentException("Username and Platform are required");
    }

    OwnerDto owner = ownerService.getOwner();
    if(owner == null) throw new IllegalStateException("Could not retrieve owner");

    // Initialize the new user
    User.UserBuilder userBuilder = User.builder()
        .platform(partialUserInfo.getPlatform())
        .username(partialUserInfo.getUsername())
        .id(partialUserInfo.getId());

    // Initialize the new security details
    UserSecurityDetails originalSecurity = partialUserInfo.getUserSecurityDetails();
    UserSecurityDetails.UserSecurityDetailsBuilder newUserSecurityDetailsBuilder =
        UserSecurityDetails.builder()
          .broadcaster(originalSecurity.getBroadcaster())
          .moderator(originalSecurity.getModerator())
          .patron(originalSecurity.getPatron())
          .patronPeriod(originalSecurity.getPatronPeriod())
          .following(originalSecurity.getFollowing());

    // Add any local groups if they exist
    LocalUserDto localUserDto = localUserService.findByOwnerAndPlatformAndUsername(
      owner.getId(),
      partialUserInfo.getPlatform(),
      partialUserInfo.getUsername()).orElse(new LocalUserDto());
    Set<LocalGroupDto> groups = localUserDto.getGroups();
    groups.addAll(partialUserInfo.getGroups());
    userBuilder.groups(groups);

    // If Twitch and ID is present, add the follower details
    // TODO add caching to the twitch api class, especially for getting user ids
    if (partialUserInfo.getPlatform().equals(Platform.TWITCH)) {

      // Assume not following, by default
      newUserSecurityDetailsBuilder.following(false);

      // get the broadcaster account and proceed if it exists
      localUserService.findBroadcasterForOwnerAndPlatform(owner.getId(), Platform.TWITCH)
          .ifPresent((broadcaster) -> {

            // Get the broadcaster's id
            String broadcasterId = twitchApiClient.getUserIdFromUsername(broadcaster.getUsername());

            // If the user id isn't already present, try to get it and set it
            String userId = partialUserInfo.getId() != null
                ? partialUserInfo.getId()
                : twitchApiClient.getUserIdFromUsername(partialUserInfo.getUsername());
            userBuilder.id(userId);

            // Add the follower info to the user
            newUserSecurityDetailsBuilder.following(
                twitchApiClient.isFollowing(broadcasterId, userId)
            );

          });
    }

    // Add security details to the builder
    userBuilder.userSecurityDetails(newUserSecurityDetailsBuilder.build());

    // Return the completed User
    return userBuilder.build();

  }
}
