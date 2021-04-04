package com.tinatiel.obschatbot.core.client.twitch.chat;

import com.tinatiel.obschatbot.core.user.UserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Month;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class TwitchChatClientTagsParserTest {

    TwitchChatClientTagsParser tagsParser;

    @BeforeEach
    void setUp() {
        tagsParser = new TwitchChatClientTagsParser();
    }

    @Test
    void parseTagsAsExpected() {
        // Given some tags from Twitch
        HashMap<String, String> tags = new HashMap<>();
        // badge-info=45,
        // badges=moderator/1,subscriber/1
        tags.put("badge-info", "45");
        tags.put("badges", "moderator/1,subscriber/1");

        // When parsed
        UserDetails userDetails = tagsParser.getDetailsFromTags(tags);

        // Then the expected user details are returned
        UserDetails expectedUserDetails = UserDetails.builder()
                .moderator(true)
                .patreon(true)
                .patreonDuration(Period.ofMonths(45))
                .build();
        assertThat(userDetails).usingRecursiveComparison().isEqualTo(expectedUserDetails);

    }

    @Test
    void returnDefaultsForNulls() {

        // Given some tags from Twitch
        HashMap<String, String> tags = new HashMap<>();
        // badge-info=,
        // badges=
        tags.put("badge-info", "");
        tags.put("badges", "");

        // When parsed
        UserDetails userDetails = tagsParser.getDetailsFromTags(tags);

        // Then the expected user details are returned
        UserDetails expectedUserDetails = UserDetails.builder()
                .moderator(false)
                .patreon(false)
                .patreonDuration(Period.ZERO)
                .build();
        assertThat(userDetails).usingRecursiveComparison().isEqualTo(expectedUserDetails);

        // And when null is provided, then the default UserDetails are provided
        assertThat(tagsParser.getDetailsFromTags(null))
                .usingRecursiveComparison().isEqualTo(UserDetails.builder().build());

    }

}
