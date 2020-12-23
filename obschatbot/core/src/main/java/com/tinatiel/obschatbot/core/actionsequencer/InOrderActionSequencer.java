package com.tinatiel.obschatbot.core.actionsequencer;

import com.tinatiel.obschatbot.core.action.Action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        if(reversed) Collections.reverse(result);
        return result;
    }

    @Override
    public List<Action> getActions() {
        return actions;
    }
}
