package com.tinatiel.obschatbot.core.user;

import java.time.Period;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Encompasses the security-relevant attributes of an user on a specific platform, e.g. if they are
 * following or not, paying money, moderate the channel, etc. These attributes may optionally be
 * used by the broadcaster to gate access to specific commands, for example reserving special
 * commands only for patrons and a smaller set for guests.
 */
@Builder
@Getter
@ToString
public class UserSecurityDetails {

  /**
   * Designates if a viewer is 'following' a broadcaster ('follow' on Twitch, 'subscribe' on
   * YouTube).
   */
  private final Boolean following;

  /**
   * Designates if a viewer is paying money to the broadcaster (according to the broadcaster's
   * platform), for example 'subscribers' on Twitch or 'join/member' on YouTube.
   */
  private final Boolean patron;

  /**
   * Designates how long (day resolution) a patron has been paying money to the broadcaster.
   */
  private final Period patronPeriod;

  /**
   * Designates if a viewer was granted moderator privileges by the broadcaster.
   */
  private final Boolean moderator;

  /**
   * Designates if the viewer is the broadcaster, or if the given viewer should have permission to
   * execute any command.
   */
  private final Boolean broadcaster;
}
