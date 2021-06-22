package com.tinatiel.obschatbot.core.user;

import com.tinatiel.obschatbot.data.localuser.model.LocalUserDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalUserDtoTest {

    @Test
    void notBroadcasterByDefault() {

        // Given a default User
        LocalUserDto localUserDto = LocalUserDto.builder().build();

        // It is not a broadcaster
        assertThat(localUserDto.isBroadcaster()).isFalse();

    }

    @Test
    void groupsEmptyByDefault() {

        // Given a default User
        LocalUserDto localUserDto = LocalUserDto.builder().build();
        LocalUserDto localUserDto2 = new LocalUserDto();

        // It is not a broadcaster
        assertThat(localUserDto.getGroups()).isNotNull().isEmpty();
        assertThat(localUserDto2.getGroups()).isNotNull().isEmpty();

    }

    @Test
    void differentUsernameSamePlatformAreDifferentUsers() {

        // given two users from same platform, but different names
        LocalUserDto mango = LocalUserDto.builder().platform(Platform.TWITCH).username("mango").build();
        LocalUserDto tina = LocalUserDto.builder().platform(Platform.TWITCH).username("tina").build();

        // then they aren't the same user
        assertThat(tina).isNotEqualTo(mango);

    }

    @Test
    void differentPlatformSameUsernameAreDifferentUsers() {

        // given two users from same platform, but different names
        LocalUserDto localTina = LocalUserDto.builder().platform(Platform.LOCAL).username("tina").build();
        LocalUserDto tina = LocalUserDto.builder().platform(Platform.TWITCH).username("tina").build();

        // then they aren't the same user
        assertThat(tina).isNotEqualTo(localTina);

    }

    @Test
    void samePlatformAndSameUsernameIsSameUser() {

        // Given copy of users on same platform
        LocalUserDto mango = LocalUserDto.builder().platform(Platform.TWITCH).username("mango").build();
        LocalUserDto mangoCopy = LocalUserDto.builder().platform(Platform.TWITCH).username("mango").build();

        // Then they are the same user
        assertThat(mango).isEqualTo(mangoCopy);
        assertThat(mango).isNotSameAs(mangoCopy);

    }

}
