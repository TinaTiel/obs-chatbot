package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.request.CommandRequest;
import com.tinatiel.obschatbot.core.user.UserType;

import java.util.Arrays;
import java.util.List;

/**
 * A {@link WorkGroupRouter} that routes requests based on the type of User making the
 * request. Requests are routed by broadcaster, moderator, or other -- the intention being
 * the broadcaster interrupts moderators, and moderators interrupt everyone else.
 */
public class BroadcasterModeratorElseWorkGroupRouter implements WorkGroupRouter {

    private final WorkGroup broadcasterWg;
    private final WorkGroup moderatorWg;
    private final WorkGroup otherWg;

    public BroadcasterModeratorElseWorkGroupRouter(WorkGroup broadcasterWg, WorkGroup moderatorWg, WorkGroup otherWg) {
        this.broadcasterWg = broadcasterWg;
        this.moderatorWg = moderatorWg;
        this.otherWg = otherWg;
    }

    @Override
    public void route(CommandRequest commandRequest) {

        // Defensively get the userType
        UserType userType = commandRequest.getContext().getUser().getUserType();
        if(userType == null) userType = UserType.GUEST;

        // Route based on userType to the workgroups
        switch (userType) {
            case BROADCASTER:
                broadcasterWg.add(commandRequest);
                break;
            case MODERATOR:
                moderatorWg.add(commandRequest);
                break;
            default:
                otherWg.add(commandRequest);
        }
    }

    @Override
    public List<WorkGroup> workGroupsByPriority() {
        return Arrays.asList(broadcasterWg, moderatorWg, otherWg);
    }
}
