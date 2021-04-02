package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.request.CommandRequest;

import java.util.List;

/**
 * Responsible for routing requests to the correct WorkGroup (internally, in implementations),
 * and for returning those same WorkGroups in priority-order. Implementations can be simple (such
 * as with {@link BroadcasterModeratorElseWorkGroupRouter} that always route to fixed groups, or
 * complex and route based on provided expressions etc. supplied during runtime.
 */
public interface WorkGroupRouter {
    void route(CommandRequest commandRequest);
    List<WorkGroup> workGroupsByPriority();
}
