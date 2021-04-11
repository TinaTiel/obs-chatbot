package com.tinatiel.obschatbot.core.request;

import com.tinatiel.obschatbot.core.messaging.AbstractObsChatbotEvent;
import java.util.UUID;

/**
 * Designates that an action has been completed, for example that a "wait" has finished timeout
 * or that a third-party has acknowledged a request has finished. Clients must emit this event
 * the action has been completed.
 */
public class ActionCompleteEvent extends AbstractObsChatbotEvent {

  private final UUID completedActionRequestId;

  public ActionCompleteEvent(UUID completedActionRequestId) {
    super();
    this.completedActionRequestId = completedActionRequestId;
  }

  public UUID getCompletedActionRequestId() {
    return completedActionRequestId;
  }

  @Override
  public String toString() {
    return "ActionCompleteEvent{"
      + super.toString()
      + "completedActionRequestId=" + completedActionRequestId
      + '}';
  }
}
