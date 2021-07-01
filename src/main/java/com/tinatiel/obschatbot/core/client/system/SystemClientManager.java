package com.tinatiel.obschatbot.core.client.system;

import com.tinatiel.obschatbot.core.action.model.WaitAction;
import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import com.tinatiel.obschatbot.core.request.ActionCompleteEvent;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import com.tinatiel.obschatbot.core.request.messaging.ActionRequestStatusGateway;
import java.util.Timer;
import java.util.TimerTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;

/**
 * An implementation of ClientManager that handles action requests for Waits and other system calls.
 */
@Slf4j
public class SystemClientManager implements ClientManager {

  private final ActionRequestStatusGateway actionRequestStatusGateway;
  private Timer timer = new Timer();

  public SystemClientManager(
      ActionRequestStatusGateway actionRequestStatusGateway) {
    this.actionRequestStatusGateway = actionRequestStatusGateway;
  }

  @Override
  public void startClient() {
    // do nothing
  }

  @Override
  public void stopClient() {
    // do nothing
  }

  @Override
  public void reloadClient() {
    // do nothing
  }

  @Override
  public void onLifecycleEvent(ObsChatbotEvent lifecycleEvent) {
    // do nothing
  }

  @ServiceActivator(inputChannel = "actionRequestChannel")
  @Override
  public void onActionRequest(ActionRequest actionRequest) {
    if (actionRequest.getAction() instanceof WaitAction) {
      log.debug("Consuming WAIT: " + actionRequest);
      long waitMills = ((WaitAction) actionRequest.getAction()).getWaitDuration().toMillis();
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          log.debug("WAIT completed");
          actionRequestStatusGateway.submit(new ActionCompleteEvent(actionRequest.getId()));
        }
      }, waitMills);
    }
  }
}
