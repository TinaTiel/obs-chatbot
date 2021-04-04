package com.tinatiel.obschatbot.core.user;

import com.tinatiel.obschatbot.core.client.twitch.api.TwitchApiClient;
import com.tinatiel.obschatbot.core.user.local.LocalUser;
import com.tinatiel.obschatbot.core.user.local.LocalUserRepository;
import com.tinatiel.obschatbot.core.user.local.UserGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    LocalUserRepository localUserRepository;
    TwitchApiClient twitchApiClient;

    UserService userService;

    @BeforeEach
    void setUp() {
        localUserRepository = mock(LocalUserRepository.class);
        twitchApiClient = mock(TwitchApiClient.class);

        userService = new UserServiceImpl(localUserRepository, twitchApiClient);
    }

    @Test
    void platformAndUsernameAlwaysRequired() {

        assertThatThrownBy(() -> {
            userService.findUserFromPartial(User.builder().platform(Platform.TWITCH).build());
        }).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> {
            userService.findUserFromPartial(User.builder().username("foo").build());
        }).isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void groupMembershipsAreAddedToUser() {

        // Given a partial User
        User partialUser = User.builder()
                .platform(Platform.TWITCH)
                .username("garfield")
                .build();

        // And given an user has group memberships
        Set<UserGroup> expectedGroups = new HashSet<>(Arrays.asList(
                UserGroup.builder().name("raffle winners").build(),
                UserGroup.builder().name("regulars").build(),
                UserGroup.builder().name("obviously a cat").build()
        ));
        when(localUserRepository.findByPlatformAndUsername(any(), any())).thenReturn(
                Optional.of(LocalUser.builder()
                    .username(partialUser.getUsername())
                        .platform(partialUser.getPlatform())
                        .groups(expectedGroups)
                    .build()
                )
        );

        // When retrieved
        User fullUser = userService.findUserFromPartial(partialUser);

        // Then the full user has those memberships
        assertThat(fullUser.getGroups()).containsExactlyInAnyOrderElementsOf(expectedGroups);

    }

    @Disabled
    @Test
    void twitchUserAddsFollowerDetailsToExistingDetails() {

        // Given existing details...

        fail("to do");
    }

}
