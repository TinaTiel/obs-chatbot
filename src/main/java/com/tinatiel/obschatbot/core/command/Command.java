/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.command;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.sequencer.ActionSequencer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a (1) named, (2) sequence of (3) actions to execute. Commands must always be unique
 * by name, as this is how they are referred to for example in a chat application. The sequence
 * represents the order (or portion) of actions that will be executed, for example this could be
 * in an order, or picked at random. ${@link Action}s represent the actual work to be done, for
 * example sending a chat message, turning on the lights, showing a scene in OBS, etc.
 */
public class Command {

  private String name;
  private ActionSequencer actionSequencer;
  private boolean disabled = false;

  public Command() {
  }

  public Command name(String name) {
    this.name = name;
    return this;
  }

  public Command actionSequencer(ActionSequencer actionSequencer) {
    this.actionSequencer = actionSequencer;
    return this;
  }

  public Command disabled(boolean disabled) {
    this.disabled = disabled;
    return this;
  }

  public String getName() {
    return name;
  }

  public ActionSequencer getActionSequencer() {
    return actionSequencer;
  }

  public List<Action> getActions() {
    return new ArrayList<>(actionSequencer.listAll());
  }

  public boolean isDisabled() {
    return disabled;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Command command = (Command) o;
    return Objects.equals(name, command.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
