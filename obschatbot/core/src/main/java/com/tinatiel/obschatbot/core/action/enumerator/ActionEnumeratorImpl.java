package com.tinatiel.obschatbot.core.action.enumerator;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.ActionContext;
import com.tinatiel.obschatbot.core.command.Command;

import java.util.ArrayList;
import java.util.List;

public class ActionEnumeratorImpl implements ActionEnumerator {
    @Override
    public List<Action> enumerate(Command command, ActionContext context) throws CyclicalActionsException {
        List<Action> results = new ArrayList<>();
        for(Action action:command.getActionSequencer().nextSequence()) {
            results.add(action);
        }
        return null;
    }

    @Override
    public void checkForCyclicalActions(Command command) {
        List<Action> results = new ArrayList<>();
        for(Action action:command.getActionSequencer().listAll()) {
            results.add(action);
        }
    }
}
