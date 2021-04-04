package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.request.CommandRequest;
import com.tinatiel.obschatbot.core.user.UserSecurityDetails;
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

        // Defensively get the security details, defaulting to lowest security is null
        UserSecurityDetails securityDetails = commandRequest.getContext().getUser().getUserSecurityDetails();
        if(securityDetails == null) securityDetails = UserSecurityDetails.builder().build();

        // Route based on userType to the workgroups
        if(securityDetails.getBroadcaster() != null && securityDetails.getBroadcaster() ) {
            broadcasterWg.add(commandRequest);
        } else if (securityDetails.getModerator() != null && securityDetails.getModerator() ) {
            moderatorWg.add(commandRequest);
        } else {
            otherWg.add(commandRequest);
        }

    }

    @Override
    public List<WorkGroup> workGroupsByPriority() {
        return Arrays.asList(broadcasterWg, moderatorWg, otherWg);
    }
}
