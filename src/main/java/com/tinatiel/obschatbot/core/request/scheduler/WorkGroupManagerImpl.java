package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.request.CommandRequest;

/**
 * An implementation of WorkGroupManager that delegates routing and priority-ordering of
 * CommandRequests to an ${@link WorkGroupRouter}. Instead, the WorkGroupRouter routes requests
 * to the appropriate WorkGroup and is responsible for returning the WorkGroups in priority order.
 * The WorkGroupManagerImpl, in turn, pulls these WorkGroups from the router -- finding the first
 * that has work that can be done.
 *
 * @see WorkGroupRouter
 * @see WorkGroup
 */
public class WorkGroupManagerImpl implements WorkGroupManager {

  private final WorkGroup noWork = new NoWorkAvailableWorkGroup();
  private final WorkGroupRouter router;

  public WorkGroupManagerImpl(WorkGroupRouter router) {
    this.router = router;
  }

  @Override
  public WorkGroup getNext() {

    return router.workGroupsByPriority().stream()
      .filter(it -> it.getNumberOfWorkableRequests() > 0)
      .findFirst()
      .orElse(noWork);

  }

  @Override
  public void route(CommandRequest commandRequest) {
    router.route(commandRequest);
  }
}
