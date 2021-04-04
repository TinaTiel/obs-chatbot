package com.tinatiel.obschatbot.core.user;

import org.junit.jupiter.api.Test;

import java.time.Period;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserSecurityDetailsTest {

    @Test
    void defaultsAreNulls() {

        // Given a default security details
        UserSecurityDetails userSecurityDetails = UserSecurityDetails.builder().build();

        // Then it isn't a broadcaster, or moderator, etc.
        assertThat(userSecurityDetails.getBroadcaster()).isNull();
        assertThat(userSecurityDetails.getModerator()).isNull();
        assertThat(userSecurityDetails.getPatron()).isNull();
        assertThat(userSecurityDetails.getPatronPeriod()).isNull();
        assertThat(userSecurityDetails.getFollowing()).isNull();

    }
}
