package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.request.CommandRequest;

/**
 * Responsible for determining the next work group that should be executed. Implementations might,
 * for example, always pick a high priority / interrupting WorkGroup until no work remains, and then
 * pick from the next priority work group, and so on.
 */
public interface WorkGroupManager {

  /**
   * Gets the next work group in priority order having work to do. Never returns null; if there is
   * no work to do, a special {@link NoWorkAvailableWorkGroup} is returned so the next caller does
   * not need to worry about NullPointerExceptions.
   */
  WorkGroup getNext();

  /**
   * Routes a given request to the correct WorkGroup.
   */
  void route(CommandRequest commandRequest);

}
