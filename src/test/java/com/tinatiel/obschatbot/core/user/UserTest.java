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
    void defaultUserTypeIsGuest() {

        // Given a new user
        User user = User.builder().build();

        // default type is guest
        assertThat(user.getUserType()).isEqualTo(UserType.GUEST);

    }

    @Test
    void sameUsernameDifferentPlatformAreDifferentUsers() {

        // given two users, from different platforms
        User twitchUser = new User(Platform.TWITCH, "mango", UserType.MODERATOR, new HashSet<>());
        User localUser = new User(Platform.LOCAL, "mango", UserType.MODERATOR, new HashSet<>());

        // then they aren't the same user
        assertThat(twitchUser).isNotEqualTo(localUser);

    }

    @Test
    void differentUsernameSamePlatformAreDifferentUsers() {

        // given two users from same platform, but different names
        User mango = new User(Platform.TWITCH, "mango", UserType.MODERATOR, new HashSet<>());
        User tina = new User(Platform.TWITCH, "tina", UserType.BROADCASTER, new HashSet<>());

        // then they aren't the same user
        assertThat(tina).isNotEqualTo(mango);

    }

    @Test
    void samePlatformAndSameUsernameIsSameUser() {

        // Given copy of users on same platform
        User mango = new User(Platform.TWITCH, "mango", UserType.MODERATOR, new HashSet<>());
        User mangoCopy = new User(Platform.TWITCH, "mango", UserType.MODERATOR, new HashSet<>());

        // Then they are the same user
        assertThat(mango).isEqualTo(mangoCopy);
        assertThat(mango).isNotSameAs(mangoCopy);

    }
}
