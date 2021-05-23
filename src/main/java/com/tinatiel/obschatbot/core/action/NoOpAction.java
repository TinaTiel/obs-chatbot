package com.tinatiel.obschatbot.core.action;

public class NoOpAction implements Action<NoOpAction> {

  @Override
  public NoOpAction clone() {
    return new NoOpAction();
  }

  @Override
  public boolean requiresCompletion() {
    return false;
  }

  @Override
  public long getTimeout() {
    return 0;
  }
}
