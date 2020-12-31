/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.sequencer;

import com.tinatiel.obschatbot.core.action.RunnableAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomOrderActionSequencer implements ActionSequencer {

    private final List<RunnableAction> original = new ArrayList<>();
    private final List<RunnableAction> available = new ArrayList<>();
    private final List<RunnableAction> sequenced = new ArrayList<>();
    private final Integer pickedPerExecution;
    private final Random random = new Random();

    public RandomOrderActionSequencer(List<RunnableAction> actions, Integer pickedPerExecution) {
        // Sanitize
        if(actions == null) {
            throw new IllegalArgumentException("actions cannot be null");
        }

        // Initialize the pool of actions to pick from
        original.addAll(actions);
        available.addAll(actions);

        // Determine how many to pick from the pool each execution
        if(pickedPerExecution == null || pickedPerExecution > actions.size()) {
            this.pickedPerExecution = actions.size();
        } else {
            this.pickedPerExecution = pickedPerExecution;
        }

    }

    @Override
    public List<RunnableAction> nextSequence() {

        // Init empty list of picks
        List<RunnableAction> picked = new ArrayList<>();

        // Pick actions to sequence
        for(int i=0; i<pickedPerExecution; i++) {

            // If there's nothing available, then repopulate the list
            if(available.isEmpty()) {
                available.addAll(sequenced);
                sequenced.clear();
            }

            RunnableAction pick = available.remove(random.nextInt(available.size()));
            sequenced.add(pick);
            picked.add(pick);

        }

        // Return what was picked
        return new ArrayList<>(picked);

    }

    @Override
    public List<RunnableAction> listAll() {
        return original;
    }
}
