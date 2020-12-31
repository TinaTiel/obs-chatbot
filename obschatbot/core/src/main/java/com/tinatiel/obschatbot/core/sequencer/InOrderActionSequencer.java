/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.sequencer;

import com.tinatiel.obschatbot.core.action.RunnableAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InOrderActionSequencer implements ActionSequencer {

    private final List<RunnableAction> actions = new ArrayList<>();
    private final boolean reversed;

    public InOrderActionSequencer(List<RunnableAction> actions, boolean reversed) {
        this.actions.addAll(actions);
        this.reversed = reversed;
    }

    @Override
    public List<RunnableAction> nextSequence() {
        List<RunnableAction> result = new ArrayList<>(actions);
        if(reversed) Collections.reverse(result);
        return result;
    }

    @Override
    public List<RunnableAction> listAll() {
        return actions;
    }
}
