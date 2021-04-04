package com.tinatiel.obschatbot.core.user;

import com.tinatiel.obschatbot.core.user.local.LocalUser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalUserTest {

    @Test
    void notBroadcasterByDefault() {

        // Given a default User
        LocalUser localUser = LocalUser.builder().build();

        // It is not a broadcaster
        assertThat(localUser.isBroadcaster()).isFalse();

    }

    @Test
    void groupsEmptyByDefault() {

        // Given a default User
        LocalUser localUser = LocalUser.builder().build();
        LocalUser localUser2 = new LocalUser();

        // It is not a broadcaster
        assertThat(localUser.getGroups()).isNotNull().isEmpty();
        assertThat(localUser2.getGroups()).isNotNull().isEmpty();

    }

    @Test
    void differentUsernameSamePlatformAreDifferentUsers() {

        // given two users from same platform, but different names
        LocalUser mango = LocalUser.builder().platform(Platform.TWITCH).username("mango").build();
        LocalUser tina = LocalUser.builder().platform(Platform.TWITCH).username("tina").build();

        // then they aren't the same user
        assertThat(tina).isNotEqualTo(mango);

    }

    @Test
    void differentPlatformSameUsernameAreDifferentUsers() {

        // given two users from same platform, but different names
        LocalUser localTina = LocalUser.builder().platform(Platform.LOCAL).username("tina").build();
        LocalUser tina = LocalUser.builder().platform(Platform.TWITCH).username("tina").build();

        // then they aren't the same user
        assertThat(tina).isNotEqualTo(localTina);

    }

    @Test
    void samePlatformAndSameUsernameIsSameUser() {

        // Given copy of users on same platform
        LocalUser mango = LocalUser.builder().platform(Platform.TWITCH).username("mango").build();
        LocalUser mangoCopy = LocalUser.builder().platform(Platform.TWITCH).username("mango").build();

        // Then they are the same user
        assertThat(mango).isEqualTo(mangoCopy);
        assertThat(mango).isNotSameAs(mangoCopy);

    }

}
