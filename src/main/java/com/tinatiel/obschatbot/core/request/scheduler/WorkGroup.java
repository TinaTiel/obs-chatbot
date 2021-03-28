package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.CommandRequest;

import java.util.List;

/**
 * Responsible for providing the next batch of ActionRequests to process.
 * New requests can be added, existing actions can be 'freed' (see below),
 * and the number of in-flight requests can be reported. Note that because
 * some actions may pend a completion signal, it is possible for the number of
 * inflight requests to be >0 despite getting empty batches.
 *
 * Implementation may maintain a list of ActionRequest queues (built from CommandRequests).
 * This includes returning the next list of ActionRequests to execute (excluding in-process
 * ActionRequests, for example), freeing up ActionRequests that have been completed, and
 * removing ActionRequest queues that are empty or have timed out.
 */
public interface WorkGroup {

    /**
     * Add a new CommandRequest to the work group.
     */
    void add(CommandRequest commandRequest);

    /**
     * An ActionRequest might block future execution of remaining items in a
     * CommandRequest, pending its completion. This method unblocks that item.
     */
    void free(ActionRequest actionRequest);

    /**
     * Gets the next batch of ActionRequests to work on.
     * Implementations might ignore blocked CommandRequest actions, for example.
     */
    List<ActionRequest> getNextWorkBatch();

    /**
     * Gets the number of requests currently handled by the work group.
     */
    int getNumberOfInflightRequests();

}
