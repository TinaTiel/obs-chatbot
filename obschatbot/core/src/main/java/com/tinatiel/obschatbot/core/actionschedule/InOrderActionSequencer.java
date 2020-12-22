package com.tinatiel.obschatbot.core.actionschedule;

import com.tinatiel.obschatbot.core.action.Action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InOrderActionSequencer implements ActionSequencer {

    private final List<Action> actions = new ArrayList<>();

    public InOrderActionSequencer(List<Action> actions, boolean reversed) {
        this.actions.addAll(actions);
        if(reversed) Collections.reverse(this.actions);
    }

    @Override
    public List<Action> nextSequence() {
        return new ArrayList<>(actions);
    }
}
