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
  private Integer pickedPerExecution;
  private final Random random = new Random();

  /**
   * Create a new instance of this sequencer.
   *
   * @param pickedPerExecution Number of actions to pick each invocation. If null or negative, it
   *                           picks from the entire list of Actions.
   */
  public RandomOrderActionSequencer(Integer pickedPerExecution) {
    this.pickedPerExecution = pickedPerExecution;
  }

  @Override
  public List<Action> nextSequence() {

    // Set pick per execution if unset
    if (pickedPerExecution == null || pickedPerExecution > original.size()) {
      pickedPerExecution = original.size();
    }

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

  @Override
  public void setActions(List<Action> actions) {
    original.addAll(actions);
    available.addAll(actions);
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
