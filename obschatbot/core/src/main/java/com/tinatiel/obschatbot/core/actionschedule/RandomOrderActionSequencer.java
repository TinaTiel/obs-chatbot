package com.tinatiel.obschatbot.core.actionschedule;

import com.tinatiel.obschatbot.core.action.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomOrderActionSequencer implements ActionSequencer {

    private List<Action> available = new ArrayList<>();
    private List<Action> executed = new ArrayList<>();
    private final Integer pickedPerExecution;
    private final Random random = new Random();

    public RandomOrderActionSequencer(List<Action> actions, Integer pickedPerExecution) {
        this.available.addAll(actions);
        this.pickedPerExecution = pickedPerExecution;
    }

    @Override
    public List<Action> nextSequence() {
        return null;
    }
}
