package com.tinatiel.obschatbot.core.action.model;

import com.tinatiel.obschatbot.core.action.Action;
import java.time.Duration;

/**
 * An Action that introduces an artificial wait/delay during an action sequence, for example
 * adding a wait when showing a media source in OBS before hiding it again.
 */
public class WaitAction implements Action<WaitAction> {

  private final Duration waitDuration;
  private final Duration waitTimeout;

  public WaitAction(Duration waitDuration) {
    this.waitDuration = waitDuration;
    this.waitTimeout = waitDuration.plus(Duration.ofSeconds(10));
  }

  public WaitAction(Duration waitDuration, Duration waitTimeout) {
    this.waitDuration = waitDuration;
    this.waitTimeout = waitTimeout;
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
    return (waitDuration.toMillis());
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
