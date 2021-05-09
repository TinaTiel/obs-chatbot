package com.tinatiel.obschatbot.core.action.model;

import com.tinatiel.obschatbot.core.action.Action;
import java.time.Duration;

public class WaitAction implements Action<WaitAction> {

  private final Duration waitDuration;

  public WaitAction(Duration waitDuration) {
    this.waitDuration = waitDuration;
  }

  @Override
  public WaitAction clone() {
    return new WaitAction(waitDuration);
  }

  @Override
  public boolean requiresCompletion() {
    return true;
  }

  @Override
  public long getTimeout() {
    // timeout one second more than duration, TODO make dynamic?
    return (waitDuration.plus(Duration.ofSeconds(10l))).toMillis();
  }

  public Duration getWaitDuration() {
    return waitDuration;
  }

  @Override
  public String toString() {
    return "WaitAction{"
      + "waitDuration=" + waitDuration
      + '}';
  }
}
