package com.tinatiel.obschatbot.core.actionschedule;

import com.tinatiel.obschatbot.core.action.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomOrderActionSequencer implements ActionSequencer {

    private List<Action> available = new ArrayList<>();
    private List<Action> sequenced = new ArrayList<>();
    private final Integer pickedPerExecution;
    private final Random random = new Random();

    public RandomOrderActionSequencer(List<Action> actions, Integer pickedPerExecution) {
        // Sanitize
        if(actions == null) {
            throw new IllegalArgumentException("actions cannot be null");
        }

        // Initialize the pool of actions to pick from
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

        for(int i=0; i<pickedPerExecution; i++) {

            // If there's nothing available, then repopulate the list
            if(available.size() <= 0) {
                available.addAll(sequenced);
                sequenced.clear();
            }

            // Pick an com.tinatiel.obschatbot.core.action from what's available to execute, and execute it
            Action picked = available.remove(random.nextInt(available.size()));

            // Add the executed com.tinatiel.obschatbot.core.action to the list of executed actions
            sequenced.add(picked);
        }

        return new ArrayList<>(sequenced);

    }
}
