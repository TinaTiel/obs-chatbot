/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.user;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {

    @Test
    void defaultSecurityIsDefaultSecurityDetails() {

        // Given a new user
        User user = User.builder().build();

        // default type is guest
        assertThat(user.getUserSecurityDetails())
                .usingRecursiveComparison()
                .isEqualTo(UserSecurityDetails.builder().build());

    }

    @Test
    void sameUsernameDifferentPlatformAreDifferentUsers() {

        // given two users, from different platforms
        User twitchUser = User.builder().platform(Platform.TWITCH).username("mango").build();
        User localUser = User.builder().platform(Platform.LOCAL).username("mango").build();

        // then they aren't the same user
        assertThat(twitchUser).isNotEqualTo(localUser);

    }

    @Test
    void differentUsernameSamePlatformAreDifferentUsers() {

        // given two users from same platform, but different names
        User mango = User.builder().platform(Platform.TWITCH).username("tinatiel").build();
        User tina = User.builder().platform(Platform.TWITCH).username("mango").build();

        // then they aren't the same user
        assertThat(tina).isNotEqualTo(mango);

    }

    @Test
    void samePlatformAndSameUsernameIsSameUser() {

        // Given copy of users on same platform
        User mango = User.builder().platform(Platform.TWITCH).username("mango").build();
        User mangoCopy = User.builder().platform(Platform.TWITCH).username("mango").build();

        // Then they are the same user
        assertThat(mango).isEqualTo(mangoCopy);
        assertThat(mango).isNotSameAs(mangoCopy);

    }
}
