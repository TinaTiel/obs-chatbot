/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action.model;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.command.Command;

/**
 * An Action that allows execution of another Command.
 */
public class ExecuteCommandAction implements Action<ExecuteCommandAction> {

  private final Command target;

  /**
   * Create a new instance of this action, providing the desired Command to execute.
   */
  public ExecuteCommandAction(Command target) {
    if (target == null) {
      throw new IllegalArgumentException("arguments cannot be null");
    }
    this.target = target;
  }

  @Override
  public ExecuteCommandAction clone() {
    return new ExecuteCommandAction(target);
  }

  @Override
  public boolean requiresCompletion() {
    return false;
  }

  /**
   * This action has no timeout; always returns zero.
   */
  @Override
  public long getTimeout() {
    return 0;
  }

  public Command getTarget() {
    return target;
  }

  @Override
  public String toString() {
    return "ExecuteCommandAction{"
      + "target=" + target
      + '}';
  }
}
