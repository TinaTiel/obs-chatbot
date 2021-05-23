/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.sequencer;

import com.tinatiel.obschatbot.core.action.Action;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An ActionSequencer that is capable of picking a subset of Actions semi-randomly. This is not
 * "true" randomness, because this implementation internally tracks which actions have already been
 * picked and will guarantee that eventually all actions are picked (just in random order). This
 * guarantees, for example, that the same Action won't ever be picked many times in a row.
 */
public class RandomOrderActionSequencer implements ActionSequencer {

  private final List<Action> original = new ArrayList<>();
  private final List<Action> available = new ArrayList<>();
  private final List<Action> sequenced = new ArrayList<>();
  private final Integer pickedPerExecution;
  private final Random random = new Random();

  /**
   * Create a new instance of this sequencer.
   *
   * @param actions            Actions to pick from.
   * @param pickedPerExecution Number of actions to pick each invocation. If null or negative, it
   *                           picks from the entire list of Actions.
   */
  public RandomOrderActionSequencer(List<Action> actions, Integer pickedPerExecution) {
    // Sanitize
    if (actions == null) {
      throw new IllegalArgumentException("actions cannot be null");
    }

    // Initialize the pool of actions to pick from
    original.addAll(actions);
    available.addAll(actions);

    // Determine how many to pick from the pool each execution
    if (pickedPerExecution == null || pickedPerExecution > actions.size()) {
      this.pickedPerExecution = actions.size();
    } else {
      this.pickedPerExecution = pickedPerExecution;
    }

  }

  @Override
  public List<Action> nextSequence() {

    // Init empty list of picks
    List<Action> picked = new ArrayList<>();

    // Pick actions to sequence
    for (int i = 0; i < pickedPerExecution; i++) {

      // If there's nothing available, then repopulate the list
      if (available.isEmpty()) {
        available.addAll(sequenced);
        sequenced.clear();
      }

      Action pick = available.remove(random.nextInt(available.size()));
      sequenced.add(pick);
      picked.add(pick);

    }

    // Return what was picked
    return new ArrayList<>(picked);

  }

  @Override
  public List<Action> listAll() {
    return original;
  }

  public Integer getPickedPerExecution() {
    return pickedPerExecution;
  }

  @Override
  public String toString() {
    return "RandomOrderActionSequencer{"
      + "originalActions=" + original
      + ", pickedPerExecution=" + pickedPerExecution
      + '}';
  }
}
