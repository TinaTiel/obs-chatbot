package com.tinatiel.obschatbot.core.user;

import java.time.Period;
import java.util.List;

/**
 * Takes a list of Security Details and combines them into one (applying in-order), finally
 * resolving any null values into default values.
 */
public class UserSecurityDetailsResolver {

  UserSecurityDetails resolveFrom(List<UserSecurityDetails> securityDetailsList) {

    // Initialize the builder
    UserSecurityDetails.UserSecurityDetailsBuilder builder = UserSecurityDetails.builder();

    // apply the security details in order
    for (UserSecurityDetails securityDetails : securityDetailsList) {
      if (securityDetails.getBroadcaster() != null) {
        builder.broadcaster(securityDetails.getBroadcaster());
      }
      if (securityDetails.getModerator() != null) {
        builder.moderator(securityDetails.getModerator());
      }
      if (securityDetails.getPatron() != null) {
        builder.patron(securityDetails.getPatron());
      }
      if (securityDetails.getPatronPeriod() != null) {
        builder.patronPeriod(securityDetails.getPatronPeriod());
      }
      if (securityDetails.getFollowing() != null) {
        builder.following(securityDetails.getFollowing());
      }
    }

    // resolve any null values
    UserSecurityDetails maybeNullSecurityDetails = builder.build();
    boolean isBroadcaster = maybeNullSecurityDetails.getBroadcaster() == null
        ? false
        : maybeNullSecurityDetails.getBroadcaster();
    boolean isModerator = maybeNullSecurityDetails.getModerator() == null
        ? false
        : maybeNullSecurityDetails.getModerator();
    boolean isPatron =
        maybeNullSecurityDetails.getPatron() == null
          ? false
          : maybeNullSecurityDetails.getPatron();
    Period patronPeriod = maybeNullSecurityDetails.getPatronPeriod() == null
        ? Period.ZERO
        : maybeNullSecurityDetails.getPatronPeriod();
    boolean isFollowing = maybeNullSecurityDetails.getFollowing() == null
        ? false
        : maybeNullSecurityDetails.getFollowing();

    // build and return
    return UserSecurityDetails.builder()
      .broadcaster(isBroadcaster)
      .moderator(isModerator)
      .patron(isPatron)
      .patronPeriod(patronPeriod)
      .following(isFollowing)
      .build();
  }
}
