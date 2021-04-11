package com.tinatiel.obschatbot.core.client.twitch.chat;

import com.google.common.collect.ImmutableMap;
import com.tinatiel.obschatbot.core.user.UserSecurityDetails;
import java.time.Period;
import lombok.extern.slf4j.Slf4j;

/**
 * Parses tags for UserDetails information. See
 * <a href="https://dev.twitch.tv/docs/irc/tags#globaluserstate-twitch-tags">the Twitch IRC
 * docs</a> for more information.
 */
@Slf4j
public class TwitchChatClientTagsParser {

  UserSecurityDetails getDetailsFromTags(ImmutableMap<String, String> tags) {

    // Define default values if unset, since the tags are a source of truth
    // and it is safe to interpret null/absence as 'false' in this case.
    boolean moderator = false;
    boolean patron = false;
    Period patronPeriod = Period.ZERO;

    if (tags != null) {

      // Parse badges. Expected to be in format "badge/ver,badge/ver,..."
      for (String badge : tags.getOrDefault("badges", "").split(",")) {
        if (badge != null) {

          // check for moderator flag
          if (badge.contains("moderator")) {
            moderator = true;
          }

          // check for patron flag
          if (badge.contains("subscriber")) {
            patron = true;
          }

        }
      }

      // Parse "badge info". According to docs, this is only being used
      // atm for subscriber duration
      String badgeInfos = tags.get("badge-info");
      if (badgeInfos != null) {
        // Assuming they may list in a comma-delimited fashion in the future,
        // split the string by commas
        for (String badgeInfo : badgeInfos.split(",")) {
          // Within each expect the format to be tag/amount, for example
          // subscriber/6 would mean 6 month subscription
          String[] units = badgeInfo.split("/");
          if (units.length == 2 && units[0].equals("subscriber")) {
            try {
              patronPeriod = Period.ofMonths(Integer.parseInt(units[1]));
            } catch (NumberFormatException e) {
              log.warn("Could not parse subscriber duration from tag: " + badgeInfo);
            }
          }
        }
      }

    }

    // Build and return what we have authority over
    return UserSecurityDetails.builder()
      .moderator(moderator)
      .patron(patron)
      .patronPeriod(patronPeriod)
      .build();

  }

  String getUserIdFromTags(ImmutableMap<String, String> tags) {
    return tags.get("user-id");
  }

}
