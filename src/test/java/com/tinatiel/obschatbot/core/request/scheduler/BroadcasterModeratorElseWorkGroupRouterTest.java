package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.request.CommandRequest;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.core.user.User;
import com.tinatiel.obschatbot.core.user.UserSecurityDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class BroadcasterModeratorElseWorkGroupRouterTest {

    WorkGroup broadcasterWg;
    WorkGroup moderatorWg;
    WorkGroup otherWg;

    WorkGroupRouter router;

    @BeforeEach
    void setUp() {

        broadcasterWg = new WorkGroupImpl();
        moderatorWg = new WorkGroupImpl();
        otherWg = new WorkGroupImpl();
        router = new BroadcasterModeratorElseWorkGroupRouter(broadcasterWg, moderatorWg, otherWg);

    }

    @Test
    void workGroupsReturnedInPriorityOrder() {
        assertThat(router.workGroupsByPriority()).containsExactly(
                broadcasterWg, moderatorWg, otherWg
        );
    }

    @Test
    void broadcasterRoutesAsExpected() {

        // Given there are no initial requests
        assertThat(broadcasterWg.getNumberOfInflightRequests()).isZero();
        assertThat(moderatorWg.getNumberOfInflightRequests()).isZero();
        assertThat(otherWg.getNumberOfInflightRequests()).isZero();

        // Given broadcaster requests
        User broadcaster = User.builder()
            .platform(Platform.TWITCH)
                .username("tinatiel")
                .userSecurityDetails(UserSecurityDetails.builder().broadcaster(true).build())
                .build();
        CommandRequest request1 = new CommandRequest(new RequestContext(broadcaster, new ArrayList<>()), new ArrayList<>());
        CommandRequest request2 = new CommandRequest(new RequestContext(broadcaster, new ArrayList<>()), new ArrayList<>());
        CommandRequest request3 = new CommandRequest(new RequestContext(broadcaster, new ArrayList<>()), new ArrayList<>());

        // When routed
        router.route(request1);
        router.route(request2);
        router.route(request3);

        // Then the broadcaster wg received them
        assertThat(broadcasterWg.getNumberOfInflightRequests()).isEqualTo(3);
        assertThat(moderatorWg.getNumberOfInflightRequests()).isZero();
        assertThat(otherWg.getNumberOfInflightRequests()).isZero();

    }

    @Test
    void moderatorRoutesAsExpected() {

        // Given there are no initial requests
        assertThat(broadcasterWg.getNumberOfInflightRequests()).isZero();
        assertThat(moderatorWg.getNumberOfInflightRequests()).isZero();
        assertThat(otherWg.getNumberOfInflightRequests()).isZero();

        // Given moderator requests
        User moderator = User.builder()
                .platform(Platform.TWITCH)
                .username("mango")
                .userSecurityDetails(UserSecurityDetails.builder().moderator(true).build())
                .build();
        CommandRequest request1 = new CommandRequest(new RequestContext(moderator, new ArrayList<>()), new ArrayList<>());
        CommandRequest request2 = new CommandRequest(new RequestContext(moderator, new ArrayList<>()), new ArrayList<>());
        CommandRequest request3 = new CommandRequest(new RequestContext(moderator, new ArrayList<>()), new ArrayList<>());

        // When routed
        router.route(request1);
        router.route(request2);
        router.route(request3);

        // Then the broadcaster wg received them
        assertThat(broadcasterWg.getNumberOfInflightRequests()).isZero();
        assertThat(moderatorWg.getNumberOfInflightRequests()).isEqualTo(3);
        assertThat(otherWg.getNumberOfInflightRequests()).isZero();

    }

    @Test
    void othersRouteAsExpected() {

        // Given there are no initial requests
        assertThat(broadcasterWg.getNumberOfInflightRequests()).isZero();
        assertThat(moderatorWg.getNumberOfInflightRequests()).isZero();
        assertThat(otherWg.getNumberOfInflightRequests()).isZero();

        // Given other requests
        CommandRequest request1 = new CommandRequest(new RequestContext(
                User.builder()
                        .platform(Platform.TWITCH)
                        .username("rando55")
                        .build(), new ArrayList<>()),
                new ArrayList<>());
        CommandRequest request2 = new CommandRequest(new RequestContext(
                User.builder()
                        .platform(Platform.TWITCH)
                        .username("curious77")
                        .userSecurityDetails(UserSecurityDetails.builder().following(true).build())
                        .build(), new ArrayList<>()),
                new ArrayList<>());
        CommandRequest request3 = new CommandRequest(new RequestContext(
                User.builder()
                        .platform(Platform.TWITCH)
                        .username("avidfan68")
                        .userSecurityDetails(UserSecurityDetails.builder().patron(true).build())
                        .build(), new ArrayList<>()),
                new ArrayList<>());

        // When routed
        router.route(request1);
        router.route(request2);
        router.route(request3);

        // Then the broadcaster wg received them
        assertThat(broadcasterWg.getNumberOfInflightRequests()).isZero();
        assertThat(moderatorWg.getNumberOfInflightRequests()).isZero();
        assertThat(otherWg.getNumberOfInflightRequests()).isEqualTo(3);

    }

    @Test
    void unspecifiedTypesRouteToOther() {

        // Given there are no initial requests
        assertThat(broadcasterWg.getNumberOfInflightRequests()).isZero();
        assertThat(moderatorWg.getNumberOfInflightRequests()).isZero();
        assertThat(otherWg.getNumberOfInflightRequests()).isZero();

        // Given request with usertype unspecified
        CommandRequest request = new CommandRequest(new RequestContext(
                User.builder()
                        .platform(Platform.TWITCH)
                        .username("rando55")
                        .userSecurityDetails(null)
                        .build(), new ArrayList<>()),
                new ArrayList<>());
        assertThat(request.getContext().getUser().getUserSecurityDetails()).isNull();

        // When routed
        router.route(request);

        // Then the broadcaster wg received them
        assertThat(broadcasterWg.getNumberOfInflightRequests()).isZero();
        assertThat(moderatorWg.getNumberOfInflightRequests()).isZero();
        assertThat(otherWg.getNumberOfInflightRequests()).isEqualTo(1);
    }
}
