/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.user;

import com.tinatiel.obschatbot.core.actionservice.Platform;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {

    @Test
    void sameUsernameDifferentPlatformAreDifferentUsers() {

        // given two users, from different platforms
        User twitchUser = new User(Platform.TWITCH, "mango");
        User localUser = new User(Platform.LOCAL, "mango");

        // then they aren't the same user
        assertThat(twitchUser).isNotEqualTo(localUser);

    }

    @Test
    void differentUsernameSamePlatformAreDifferentUsers() {

        // given two users from same platform, but different names
        User mango = new User(Platform.TWITCH, "mango");
        User tina = new User(Platform.TWITCH, "tina");

        // then they aren't the same user
        assertThat(tina).isNotEqualTo(mango);

    }

    @Test
    void samePlatformAndSameUsernameIsSameUser() {

        // Given copy of users on same platform
        User mango = new User(Platform.TWITCH, "mango");
        User mangoCopy = new User(Platform.TWITCH, "mango");

        // Then they are the same user
        assertThat(mango).isEqualTo(mangoCopy);
        assertThat(mango).isNotSameAs(mangoCopy);

    }
}
