package com.tinatiel.obschatbot.core.request.scheduler;

import com.tinatiel.obschatbot.core.messaging.Listener;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.CommandRequest;
import com.tinatiel.obschatbot.core.request.messaging.ActionRequestGateway;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;

/**
 * Responsible for consuming ${@link CommandRequest}s from the CommandRequest queue and producing
 * ${@link ActionRequest}s for the ActionRequest Queue (to be consumed by clients). It strives to
 * ultimately maintain order within each command, while prioritizing (and allowing interrupts) based
 * on the ${@link com.tinatiel.obschatbot.core.request.RequestContext} that requested the command.
 *
 * <p>Internally, this uses a ${@link WorkGroupManager} to route requests and to provide the next
 * batch of actions to submit to the ActionRequest Queue. It pulls batches from the WorkGroupManager
 * continuously until there is no more work to do. After this, it polls WorkGroupManager until there
 * is work to do again.
 *
 * @see WorkGroupManager
 */
@Slf4j
public class CommandRequestScheduler implements Listener<CommandRequest> {

  private final ExecutorService executorService = Executors.newSingleThreadExecutor();

  private final WorkGroupManager workGroupManager;
  //  private final QueueClient<ActionRequest> actionRequestQueueClient;
  private final ActionRequestGateway actionRequestGateway;

  /**
   * Create a new instance of the scheduler.
   *
   * @param workGroupManager     The WorkGroupManager that routes and schedules ActionReuqests.
   * @param actionRequestGateway submits requests to the ActionRequest queue
   */
  public CommandRequestScheduler(
      WorkGroupManager workGroupManager,
      ActionRequestGateway actionRequestGateway) {
    this.workGroupManager = workGroupManager;
    this.actionRequestGateway = actionRequestGateway;
    execute();
  }

  @ServiceActivator(inputChannel = "commandRequestChannel")
  @Override
  public void onEvent(CommandRequest event) {
    log.debug("Scheduler received request " + event);
    workGroupManager.route(event);
  }

  private void execute() {
    executorService.execute(() -> {
      while (true) {
        List<ActionRequest> actionRequests = workGroupManager.getNext().getNextWorkBatch();
        actionRequests.forEach(actionRequestGateway::submit);
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
