/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.sequencer;

import com.tinatiel.obschatbot.core.action.Action;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An ActionSequencer that executes a list of Actions sequentially, in forward or reverse order.
 */
public class InOrderActionSequencer implements ActionSequencer {

  private final List<Action> actions = new ArrayList<>();
  private final boolean reversed;

  public InOrderActionSequencer(List<Action> actions, boolean reversed) {
    this.actions.addAll(actions);
    this.reversed = reversed;
  }

  @Override
  public List<Action> nextSequence() {
    List<Action> result = new ArrayList<>(actions);
    if (reversed) {
      Collections.reverse(result);
    }
    return result;
  }

  @Override
  public List<Action> listAll() {
    return actions;
  }
}
