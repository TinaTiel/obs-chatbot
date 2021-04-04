package com.tinatiel.obschatbot.core.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Period;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class UserSecurityDetailsResolverTest {

    UserSecurityDetailsResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new UserSecurityDetailsResolver();
    }

    @Test
    void latestOverridesEarliest() {

        // Given some security details
        UserSecurityDetails securityDetailsTrue = UserSecurityDetails.builder()
                .broadcaster(true)
                .moderator(true)
                .patron(true)
                .patronPeriod(Period.ofMonths(3))
                .following(true)
                .build();

        UserSecurityDetails securityDetailsFalse = UserSecurityDetails.builder()
                .broadcaster(false)
                .moderator(false)
                .patron(false)
                .patronPeriod(Period.ZERO)
                .following(false)
                .build();

        UserSecurityDetails securityDetailsNull = UserSecurityDetails.builder().build();

        // When resolved, then we expect the last value to override if set
        UserSecurityDetails trueOverridesFalse = resolver.resolveFrom(Arrays.asList(
                securityDetailsNull, securityDetailsFalse, securityDetailsTrue));
        assertThat(trueOverridesFalse.getBroadcaster()).isTrue();
        assertThat(trueOverridesFalse.getModerator()).isTrue();
        assertThat(trueOverridesFalse.getPatron()).isTrue();
        assertThat(trueOverridesFalse.getPatronPeriod()).isEqualTo(Period.ofMonths(3));
        assertThat(trueOverridesFalse.getFollowing()).isTrue();

        UserSecurityDetails falseOverridesTrue = resolver.resolveFrom(Arrays.asList(
                securityDetailsNull, securityDetailsTrue, securityDetailsFalse));
        assertThat(falseOverridesTrue.getBroadcaster()).isFalse();
        assertThat(falseOverridesTrue.getModerator()).isFalse();
        assertThat(falseOverridesTrue.getPatron()).isFalse();
        assertThat(falseOverridesTrue.getPatronPeriod()).isEqualTo(Period.ZERO);
        assertThat(falseOverridesTrue.getFollowing()).isFalse();

        UserSecurityDetails nullIgnored = resolver.resolveFrom(Arrays.asList(
                securityDetailsTrue, securityDetailsNull));
        assertThat(nullIgnored.getBroadcaster()).isTrue();
        assertThat(nullIgnored.getModerator()).isTrue();
        assertThat(nullIgnored.getPatron()).isTrue();
        assertThat(nullIgnored.getPatronPeriod()).isEqualTo(Period.ofMonths(3));
        assertThat(nullIgnored.getFollowing()).isTrue();

    }

    @Test
    void nullValuesResolveToDefaultValues() {

        // Given defaults (nulls)
        UserSecurityDetails securityDetailsNull = UserSecurityDetails.builder().build();

        // When resolved
        UserSecurityDetails results = resolver.resolveFrom(Collections.singletonList(securityDetailsNull));

        // Then the details resolve to false or zero (least permissions)
        assertThat(results.getBroadcaster()).isFalse();
        assertThat(results.getModerator()).isFalse();
        assertThat(results.getPatron()).isFalse();
        assertThat(results.getPatronPeriod()).isEqualTo(Period.ZERO);
        assertThat(results.getFollowing()).isFalse();


    }

    //    @Test
//    void broadcasterAlwaysOverridesUserDetails() {
//
//        // Given a user with lesser UserDetails, but with Broadcaster type
//        User partialUser = User.builder()
//                .platform(Platform.TWITCH)
//                .username("tinatiel")
//                .userType(UserType.GUEST)
//                .
//                .userDetails(UserDetails.builder()
//                        .patreon(true)
//                        .patreonDuration(Period.ofMonths(3))
//                        .moderator(true)
//                        .build())
//                .build();
//
//        // But given user is marked as the broadcaster locally
//        when(localUserRepository.findByPlatformAndUsername(any(), any())).thenReturn(
//                Optional.of(LocalUser.builder()
//                        .username(partialUser.getUsername())
//                        .platform(partialUser.getPlatform())
//                        .broadcaster(true)
//                        .build()
//                )
//        );
//
//        // When retrieved
//        User fullUser = userService.findUserFromPartial(partialUser);
//
//        // Then the user is a broadcaster
//        assertThat(fullUser.getUserType()).isEqualTo(UserType.BROADCASTER);
//
//        // And there were no interactions with the Twitch API
//        verifyNoInteractions(twitchApiClient);
//
//    }
//
//    @Test
//    void moderatorAndSubscriberDetailsComeFromExistingDetailsForTwitch() {
//
//        // Given a partial User, not a broadcaster
//        UserDetails expectedUserDetails = UserDetails.builder()
//                .patreon(true)
//                .patreonDuration(Period.ofMonths(3))
//                .moderator(true)
//                .build();
//        User partialUser = User.builder()
//                .platform(Platform.TWITCH)
//                .username("tinatiel")
//                .userType(UserType.GUEST)
//                .userDetails(expectedUserDetails)
//                .build();
//
//        // When retrieved
//        User fullUser = userService.findUserFromPartial(partialUser);
//
//        // Then the details match as expected
//        assertThat(fullUser.getUserDetails()).usingRecursiveComparison().isEqualTo(expectedUserDetails);
//
//    }
//
//    @Test
//    void moderatorIfPresentButNotIfBroadcaster() {
//
//        // Given a partial User, with moderator
//        User partialUser = User.builder()
//                .platform(Platform.TWITCH)
//                .username("tinatiel")
//                .userType(UserType.GUEST)
//                .userDetails(UserDetails.builder()
//                        .patreon(true)
//                        .patreonDuration(Period.ofMonths(3))
//                        .moderator(true)
//                        .build())
//                .build();
//
//        // When retrieved
//        User fullUser = userService.findUserFromPartial(partialUser);
//
//        // Then the details match as expected
//        assertThat(fullUser.getUserType()).isEqualTo(UserType.MODERATOR);
//
//    }

}
