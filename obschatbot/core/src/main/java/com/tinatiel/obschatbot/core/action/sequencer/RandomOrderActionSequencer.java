package com.tinatiel.obschatbot.core.action.sequencer;

import com.tinatiel.obschatbot.core.action.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomOrderActionSequencer implements ActionSequencer {

    private final List<Action> original = new ArrayList<>();
    private final List<Action> available = new ArrayList<>();
    private final List<Action> sequenced = new ArrayList<>();
    private final Integer pickedPerExecution;
    private final Random random = new Random();

    public RandomOrderActionSequencer(List<Action> actions, Integer pickedPerExecution) {
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
    public List<Action> nextSequence() {

        // Init empty list of picks
        List<Action> picked = new ArrayList<>();

        // Pick actions to sequence
        for(int i=0; i<pickedPerExecution; i++) {

            // If there's nothing available, then repopulate the list
            if(available.isEmpty()) {
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
}
