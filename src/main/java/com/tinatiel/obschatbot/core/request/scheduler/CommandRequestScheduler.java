package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.messaging.Listener;
import com.tinatiel.obschatbot.core.messaging.QueueClient;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.CommandRequest;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for consuming ${@link CommandRequest}s from the CommandRequest queue and producing
 * ${@link ActionRequest}s for the ActionRequest Queue (to be consumed by clients).
 * It strives to ultimately maintain order within each command, while prioritizing (and allowing
 * interrupts) based on the ${@link com.tinatiel.obschatbot.core.request.RequestContext} that
 * requested the command.
 *
 * <p>Internally, this uses a ${@link WorkGroupManager} to route requests and to provide the next
 * batch of actions to submit to the ActionRequest Queue. It pulls batches from the WorkGroupManager
 * continuously until there is no more work to do. After this, it polls WorkGroupManager until
 * there is work to do again.
 *
 * @see WorkGroupManager
 */
public class CommandRequestScheduler implements Listener<CommandRequest> {

  private final Logger log = LoggerFactory.getLogger(this.getClass());
  private final ExecutorService executorService = Executors.newSingleThreadExecutor();

  private final WorkGroupManager workGroupManager;
  private final QueueClient<ActionRequest> actionRequestQueueClient;

  /**
   * Create a new instance of the scheduler.
   *
   * @param workGroupManager The WorkGroupManager that routes and schedules ActionReuqests.
   * @param actionRequestQueueClient A ${@link QueueClient} that submits requests to the
   *                                 ActionRequest queue
   */
  public CommandRequestScheduler(
      WorkGroupManager workGroupManager,
      QueueClient<ActionRequest> actionRequestQueueClient) {
    this.workGroupManager = workGroupManager;
    this.actionRequestQueueClient = actionRequestQueueClient;
    execute();
  }

  @Override
  public void onEvent(CommandRequest event) {
    workGroupManager.route(event);
  }

  private void execute() {
    executorService.execute(() -> {
      while (true) {
        List<ActionRequest> actionRequests = workGroupManager.getNext().getNextWorkBatch();
        actionRequests.forEach(actionRequestQueueClient::submit);
        if (actionRequests.isEmpty()) {
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            log.error("CommandRequestScheduler was interrupted", e);
          }
        }
      }
    });
  }
}
