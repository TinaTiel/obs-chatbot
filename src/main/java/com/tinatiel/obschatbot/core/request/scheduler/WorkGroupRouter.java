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

    /**
     * Routes a given request to its appropriate {@link WorkGroup} internally, adding it to that group.
     */
    void route(CommandRequest commandRequest);

    /**
     * Returns {@link WorkGroup}s in priority order, with the first in the list being highest
     * priority, and the last in the list being the lowest priority.
     */
    List<WorkGroup> workGroupsByPriority();
}
