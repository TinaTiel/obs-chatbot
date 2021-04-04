package com.tinatiel.obschatbot.core.client.twitch.chat;

import com.tinatiel.obschatbot.core.user.UserDetails;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Parses tags for UserDetails information. See
 * <a href="https://dev.twitch.tv/docs/irc/tags#globaluserstate-twitch-tags">the Twitch IRC docs</a>
 * for more information.
 */
@Slf4j
public class TwitchChatClientTagsParser {

    UserDetails getDetailsFromTags(HashMap<String, String> tags) {
        UserDetails.UserDetailsBuilder builder = UserDetails.builder();

        if(tags != null) {

            // Parse badges. Expected to be in format "badge/ver,badge/ver,..."
            Arrays.stream(tags.getOrDefault("badges", "").split(","))
                    .forEach(it -> {
                        if(it != null) {
                            if(it.contains("moderator")) builder.moderator(true);
                            if(it.contains("subscriber")) builder.patreon(true);
                        }
                    });

            // Parse "badge info". According to docs, this is only being used
            // atm for subscriber duration
            String badgeInfo = tags.get("badge-info");
            if(badgeInfo != null) {
                try {
                    builder.patreonDuration(
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
