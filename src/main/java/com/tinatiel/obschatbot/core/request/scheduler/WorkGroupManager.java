package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.request.ActionRequest;

/**
 * Responsible for determining the next work group that should be executed. Implementations might,
 * for example, always pick a high priority / interrupting WorkGroup until no work remains, and then
 * pick from the next priority work group, and so on.
 * Also provides the entrypoint for randomly accessing a WorkGroup given an ActionRequest (e.g. to free
 * an ActionRequest).
 */
public interface WorkGroupManager {

    /**
     * Get the workgroup corresponding to an ActionRequest. For example this might be used
     * by a DoneQueue listener to get the corresponding workGroup for a completed
     * actionRequest
     */
    WorkGroup getWorkGroupByActionRequest(ActionRequest actionRequest);

    /**
     * Add a workgroup for a given key, setting its priority.
     * Highest priority has highest precedence.
     */
    void addWorkGroup(WorkGroup workGroup);

    /**
     * Gets the next work group in priority order. For example, it may always
     * return the Broadcaster workgroup if there are items to process, then
     * the Moderator, then etc.
     */
    WorkGroup getNext();

}
