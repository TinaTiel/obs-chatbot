package com.tinatiel.obschatbot.core.client.twitch.chat;

import com.google.common.collect.ImmutableMap;
import com.tinatiel.obschatbot.core.user.UserSecurityDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Period;

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
        ImmutableMap<String, String> tags = ImmutableMap.<String, String>builder()
        // badge-info=45,
        // badges=moderator/1,subscriber/1
        .put("badge-info", "45")
        .put("badges", "moderator/1,subscriber/1")
        .build();

        // When parsed
        UserSecurityDetails userSecurityDetails = tagsParser.getDetailsFromTags(tags);

        // Then the expected user details are returned
        UserSecurityDetails expectedUserSecurityDetails = UserSecurityDetails.builder()
                .moderator(true)
                .patron(true)
                .patronPeriod(Period.ofMonths(45))
                .build();
        assertThat(userSecurityDetails)
                .usingRecursiveComparison()
                .isEqualTo(expectedUserSecurityDetails);

    }

    @Test
    void returnDefaultsForNulls() {

        // Given some tags from Twitch
        ImmutableMap<String, String> tags = ImmutableMap.<String, String>builder()
        // badge-info=,
        // badges=
        .put("badge-info", "")
        .put("badges", "")
        .build();

        // When parsed
        UserSecurityDetails userSecurityDetails = tagsParser.getDetailsFromTags(tags);

        // Then the expected user details are returned
        UserSecurityDetails expectedUserSecurityDetails = UserSecurityDetails.builder()
                .moderator(false)
                .patron(false)
                .patronPeriod(Period.ZERO)
                .build();
        assertThat(userSecurityDetails).usingRecursiveComparison().isEqualTo(expectedUserSecurityDetails);

        // And when null is provided, then the default UserDetails are provided
        assertThat(tagsParser.getDetailsFromTags(null))
                .usingRecursiveComparison().isEqualTo(expectedUserSecurityDetails);

    }

}
