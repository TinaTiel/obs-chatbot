package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.CommandRequest;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.core.user.User;
import com.tinatiel.obschatbot.core.user.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class WorkGroupManagerTest {

    WorkGroupManager workGroupManager;

    User broadcaster = new User(Platform.TWITCH, "tinatiel", UserType.BROADCASTER);
    User moderator = new User(Platform.TWITCH, "mango", UserType.MODERATOR);
    User other = new User(Platform.TWITCH, "cooldude54", UserType.GUEST);

    @BeforeEach
    void setUp() {
        workGroupManager = new WorkGroupManagerImpl();
    }

    @Test
    void broadcasterInterruptsModerator() {

        // Given requests from moderator and other
//        CommandRequest moderatorRequest = new CommandRequest(
//                new RequestContext(moderator, new ArrayList<>()),
//
//        );

        // Then moderator will be picked

        // But when a broadcaster request arrives

        // Then broadcaster will be picked

    }

    @Test
    void moderatorRunsIfBroadcasterBusy() {

    }

    @Test
    void moderatorInterruptsOther() {

    }

    @Test
    void OtherRunsIfModeratorBusy() {

    }
}
