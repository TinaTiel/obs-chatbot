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
            userService.getUserFromPartial(User.builder().platform(Platform.TWITCH).build());
        }).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> {
            userService.getUserFromPartial(User.builder().username("foo").build());
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
        User fullUser = userService.getUserFromPartial(partialUser);

        // Then the full user has those memberships
        assertThat(fullUser.getGroups()).containsExactlyInAnyOrderElementsOf(expectedGroups);

    }

    @Test
    void twitchUserAddsFollowerDetailsToExistingDetails() {

        String broadcasterid = "broadcasterid";
        String viewerid = "viewerid";

        // Given a partial User
        User partialUser = User.builder()
          .platform(Platform.TWITCH)
          .username("garfield")
          .build();

        // Given the broadcaster exists for the Twitch platform
        LocalUser broadcaster = LocalUser.builder().username("tinatiel").broadcaster(true).build();
        when(localUserRepository.findByPlatformAndBroadcasterTrue(Platform.TWITCH)).thenReturn(
          Optional.of(broadcaster)
        );

        // And given we can get the id of the broadcaster and user
        when(twitchApiClient.getUserIdFromUsername(broadcaster.getUsername())).thenReturn(broadcasterid);
        when(twitchApiClient.getUserIdFromUsername(partialUser.getUsername())).thenReturn(viewerid);

        // And given the Twitch api client gives some response (we'll say following since not is the default)
        when(twitchApiClient.isFollowing(broadcasterid, viewerid)).thenReturn(true);

        // When retrieved
        User fullUser = userService.getUserFromPartial(partialUser);

        // Then the full user is following
        assertThat(fullUser.getUserSecurityDetails().getFollowing()).isTrue();

        // And we also have the user id
        assertThat(fullUser.getId()).isEqualTo(viewerid);

    }

    @Test
    void twitchUserIgnoresFollowerDetailsWhenNoBroadcaster() {

        // Given a partial User
        User partialUser = User.builder()
          .platform(Platform.TWITCH)
          .id("1234")
          .username("garfield")
          .build();

        // Given the broadcaster does NOT exist for the Twitch platform
        when(localUserRepository.findByPlatformAndBroadcasterTrue(Platform.TWITCH)).thenReturn(
          Optional.empty()
        );

        // When retrieved
        User fullUser = userService.getUserFromPartial(partialUser);

        // Then the full user is not following
        assertThat(fullUser.getUserSecurityDetails().getFollowing()).isFalse();

    }

    @Test
    void twitchUserIgnoresFollowerDetailsWhenNoViewerOrBroadcasterId() {

        // Given a partial User
        User partialUser = User.builder()
          .platform(Platform.TWITCH)
          .id("1234")
          .username("garfield")
          .build();

        // Given the broadcaster does exist for the Twitch platform
        when(localUserRepository.findByPlatformAndBroadcasterTrue(Platform.TWITCH)).thenReturn(
          Optional.empty()
        );

        // But given we cannot retrieve the id of the broadcaster or viewer
        when(twitchApiClient.getUserIdFromUsername(any())).thenReturn(null);

        // When retrieved
        User fullUser = userService.getUserFromPartial(partialUser);

        // Then the full user is not following
        assertThat(fullUser.getUserSecurityDetails().getFollowing()).isFalse();

    }

}
