package com.tinatiel.obschatbot.core.command;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.actionschedule.ActionSequencer;

import java.util.List;

public class Command {

    private String name;
    private ActionSequencer actionSequencer;
    private boolean disabled;

    public Command() {}

    public Command name(String name) {
        this.name = name;
        return this;
    }

    public Command actionSequencer(ActionSequencer actionSequencer) {
        this.actionSequencer = actionSequencer;
        return this;
    }

    public Command disabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public String getName() {
        return name;
    }

    public ActionSequencer getActionSequencer() {
        return actionSequencer;
    }

    public List<Action> getActions() {
        return null;
    }

    public boolean isDisabled() {
        return disabled;
    }

}
