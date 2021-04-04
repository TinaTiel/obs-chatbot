package com.tinatiel.obschatbot.core.client.twitch.chat;

import com.google.common.collect.ImmutableMap;
import com.tinatiel.obschatbot.core.user.UserSecurityDetails;
import lombok.extern.slf4j.Slf4j;

import java.time.Period;
import java.util.Arrays;

/**
 * Parses tags for UserDetails information. See
 * <a href="https://dev.twitch.tv/docs/irc/tags#globaluserstate-twitch-tags">the Twitch IRC docs</a>
 * for more information.
 */
@Slf4j
public class TwitchChatClientTagsParser {

    UserSecurityDetails getDetailsFromTags(ImmutableMap<String, String> tags) {
        UserSecurityDetails.UserSecurityDetailsBuilder builder = UserSecurityDetails.builder();

        if(tags != null) {

            // Parse badges. Expected to be in format "badge/ver,badge/ver,..."
            Arrays.stream(tags.getOrDefault("badges", "").split(","))
                    .forEach(it -> {
                        if(it != null) {
                            if(it.contains("moderator")) builder.moderator(true);
                            if(it.contains("subscriber")) builder.patron(true);
                        }
                    });

            // Parse "badge info". According to docs, this is only being used
            // atm for subscriber duration
            String badgeInfo = tags.get("badge-info");
            if(badgeInfo != null) {
                try {
                    builder.patronPeriod(
                            Period.ofMonths(Integer.parseInt(badgeInfo))
                    );
                } catch (NumberFormatException e) {
                    log.warn("Could not parse subscriber duration from tag: " + badgeInfo);
                }
            }

        }

        return builder.build();
    }

}
