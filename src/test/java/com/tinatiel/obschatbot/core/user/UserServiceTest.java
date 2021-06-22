package com.tinatiel.obschatbot.core.user;

import com.tinatiel.obschatbot.core.client.twitch.api.TwitchApiClient;
import com.tinatiel.obschatbot.data.localuser.model.LocalUserDto;
import com.tinatiel.obschatbot.data.localuser.LocalUserService;
import com.tinatiel.obschatbot.data.localuser.model.LocalGroupDto;
import com.tinatiel.obschatbot.security.owner.OwnerDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    OwnerDto owner;
    OwnerService ownerService;
    LocalUserService localUserService;
    TwitchApiClient twitchApiClient;

    UserService userService;

    @BeforeEach
    void setUp() {
        owner = OwnerDto.builder()
          .name("some owner")
          .id(UUID.randomUUID())
          .build();
        ownerService = mock(OwnerService.class);
        when(ownerService.getOwner()).thenReturn(owner);
        localUserService = mock(LocalUserService.class);
        twitchApiClient = mock(TwitchApiClient.class);

        userService = new UserServiceImpl(ownerService, localUserService, twitchApiClient);
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
        Set<LocalGroupDto> expectedGroups = new HashSet<>(Arrays.asList(
                LocalGroupDto.builder().name("raffle winners").build(),
                LocalGroupDto.builder().name("regulars").build(),
                LocalGroupDto.builder().name("obviously a cat").build()
        ));
        when(localUserService.findByOwnerAndPlatformAndUsername(eq(owner.getId()), any(), any())).thenReturn(
                Optional.of(LocalUserDto.builder()
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
        LocalUserDto broadcaster = LocalUserDto.builder().username("tinatiel").broadcaster(true).build();
        when(localUserService.findBroadcasterForOwnerAndPlatform(eq(owner.getId()), eq(Platform.TWITCH)))
          .thenReturn(Optional.of(broadcaster));

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
        when(localUserService.findBroadcasterForOwnerAndPlatform(eq(owner.getId()), eq(Platform.TWITCH)))
          .thenReturn(Optional.empty());

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

        // Given the broadcaster exists for the Twitch platform
        LocalUserDto broadcaster = LocalUserDto.builder().username("tinatiel").broadcaster(true).build();
        when(localUserService.findBroadcasterForOwnerAndPlatform(eq(owner.getId()), eq(Platform.TWITCH)))
          .thenReturn(Optional.of(broadcaster));

        // But given we cannot retrieve the id of the broadcaster or viewer
        when(twitchApiClient.getUserIdFromUsername(any())).thenReturn(null);

        // When retrieved
        User fullUser = userService.getUserFromPartial(partialUser);

        // Then the full user is not following
        assertThat(fullUser.getUserSecurityDetails().getFollowing()).isFalse();

    }

}
